package net.crytec.libs.protocol.holograms.infobars;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.crytec.libs.protocol.events.InfoBarCreateEvent;
import net.crytec.libs.protocol.events.PlayerReceiveEntityEvent;
import net.crytec.libs.protocol.events.PlayerUnloadsEntityEvent;
import net.crytec.libs.protocol.tracking.EntityTracker;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;

public class InfoBarManager implements Listener {

  public InfoBarManager(final JavaPlugin host, final Function<Entity, AbstractInfoBar> barSupplier) {
    this.barSupplier = barSupplier;
    this.host = host;
    playerViews = Maps.newHashMap();
    infoBarMap = new HashMap<>();
    Bukkit.getPluginManager().registerEvents(this, host);
    hologramEntityMappings = new Int2ObjectOpenHashMap<>();
    registerPacketListener(host);
    runnable = new InfoBarRunnable(host, this);
  }

  private final InfoBarRunnable runnable;
  private final JavaPlugin host;
  private final Int2ObjectOpenHashMap<Entity> hologramEntityMappings;
  private final HashMap<Integer, AbstractInfoBar> infoBarMap;
  private final Function<Entity, AbstractInfoBar> barSupplier;
  protected final Map<Player, Set<AbstractInfoBar>> playerViews;

  public void addMapping(final int hologramEntityID, final Entity host) {
    hologramEntityMappings.put(hologramEntityID, host);
  }

  @EventHandler
  public void onJoin(final PlayerJoinEvent event) {
    playerViews.put(event.getPlayer(), Sets.newHashSet());
    runnable.addPlayer(event.getPlayer());
  }

  @EventHandler
  public void onQuit(final PlayerQuitEvent event) {
    playerViews.remove(event.getPlayer());
    runnable.removePlayer(event.getPlayer());
  }

  @EventHandler
  protected void onDismount(final EntityDismountEvent event) {
    final int entityID = event.getDismounted().getEntityId();
    final AbstractInfoBar infoBar = infoBarMap.get(entityID);
    if (infoBar == null) {
      return;
    }
    for (final Player player : event.getEntity().getWorld().getPlayers()) {
      if (EntityTracker.getEntityViewOf(player).contains(entityID)) {
        playerViews.get(player).add(infoBar);
      }
    }
  }

  @EventHandler
  protected void onMount(final EntityMountEvent event) {
    final int entityID = event.getMount().getEntityId();
    final AbstractInfoBar infoBar = infoBarMap.get(entityID);
    if (infoBar == null) {
      return;
    }
    for (final Player player : Sets.newHashSet(infoBar.viewingPlayer)) {
      infoBar.hideFrom(player);
      playerViews.get(player).remove(infoBar);
    }
  }

  @EventHandler
  protected void onEntityShowing(final PlayerReceiveEntityEvent event) {
    Bukkit.getScheduler().runTaskLater(host, () -> {
      final AbstractInfoBar bar = infoBarMap.get(event.getEntityID());
      if (bar != null) {
        if (!bar.getEntity().getPassengers().isEmpty()) {
          return;
        }
        playerViews.get(event.getPlayer()).add(bar);
      }
    }, 1L);
  }

  @EventHandler
  protected void onEntityHiding(final PlayerUnloadsEntityEvent event) {
    for (final Integer entityID : event.getEntityIDs()) {
      final AbstractInfoBar bar = infoBarMap.get(entityID);
      if (bar != null) {
        bar.hideFrom(event.getPlayer());
        playerViews.get(event.getPlayer()).remove(bar);
      }
    }
  }

  @EventHandler
  protected void onDeath(final EntityDeathEvent event) {
    final Integer entityID = event.getEntity().getEntityId();
    final AbstractInfoBar bar = infoBarMap.get(entityID);
    if (bar != null) {
      final Set<Player> viewing = Sets.newHashSet(bar.viewingPlayer);
      for (final Player viewer : viewing) {
        bar.hideFrom(viewer);
        playerViews.get(viewer).remove(bar);
      }
      infoBarMap.remove(entityID);
    }
  }

  @EventHandler
  protected void onChunkUnload(final ChunkUnloadEvent event) {
    for (final Entity entity : event.getChunk().getEntities()) {
      final Integer entityID = entity.getEntityId();
      final AbstractInfoBar bar = infoBarMap.get(entityID);
      if (bar != null) {
        for (final Player player : bar.viewingPlayer) {
          if (player.isOnline()) {
            bar.hideFrom(player);
            playerViews.get(player).remove(bar);
          }
        }
        infoBarMap.remove(entityID);
      }
    }
  }

  @Nullable
  public AbstractInfoBar getInfoBar(final Entity entity) {
    return infoBarMap.get(entity.getEntityId());
  }

  @Nullable
  public AbstractInfoBar createInfoBar(final Entity entity) {
    final Integer entityID = entity.getEntityId();
    AbstractInfoBar bar = infoBarMap.get(entityID);
    if (bar != null) {
      return bar;
    }

    final InfoBarCreateEvent event = new InfoBarCreateEvent(entity);
    event.callEvent();
    if (event.isCancelled()) {
      return bar;
    }

    bar = barSupplier.apply(entity);
    infoBarMap.put(entityID, bar);
    final List<Pair<String, InfoLineSpacing>> lines = event.getLines();

    for (int i = 0; i < lines.size(); i++) {
      final Pair<String, InfoLineSpacing> entry = lines.get(i);
      bar.addLine(entry.getLeft(), entry.getRight());
    }

    for (final Player player : entity.getWorld().getPlayers()) {
      if (EntityTracker.getEntityViewOf(player).contains(entity.getEntityId())) {
        playerViews.get(player).add(bar);
      }
    }

    return bar;
  }

  public void removeInfoBar(final Entity entity) {
    final Integer entityID = entity.getEntityId();
    final AbstractInfoBar bar = infoBarMap.get(entityID);
    if (bar == null) {
      return;
    }
    for (final Player player : bar.viewingPlayer) {
      bar.hideFrom(player);
      playerViews.get(player).remove(bar);
    }
    infoBarMap.remove(entityID);
  }

  private void registerPacketListener(final JavaPlugin plugin) {
    final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
    manager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Client.USE_ENTITY) {

      @Override
      public void onPacketReceiving(final PacketEvent event) {
        final int entityID = event.getPacket().getIntegers().getValues().get(0);
        final Entity entity = hologramEntityMappings.get(entityID);
        if (entity != null) {
          event.getPacket().getIntegers().modify(0, (old) -> entity.getEntityId());
        }
      }

    });
  }

}