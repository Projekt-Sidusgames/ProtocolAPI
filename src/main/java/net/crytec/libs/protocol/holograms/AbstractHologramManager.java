package net.crytec.libs.protocol.holograms;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import net.crytec.libs.commons.utils.UtilChunk;
import net.crytec.libs.protocol.events.PlayerReceiveChunkEvent;
import net.crytec.libs.protocol.events.PlayerUnloadsChunkEvent;
import net.crytec.libs.protocol.tracking.ChunkTracker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public abstract class AbstractHologramManager implements Listener {

  public AbstractHologramManager(final JavaPlugin host, final IHologramFactory factory) {
    this.factory = factory;
    hologramIDMap = new Object2ObjectOpenHashMap<>();
    hologramViews = new Object2ObjectOpenHashMap<>();
    entityIDMappings = new Object2ObjectOpenHashMap<>();
    entityIDinverseMappings = new Object2ObjectOpenHashMap<>();
    loadedHolograms = HashBasedTable.create();
    movingHolograms = new ObjectOpenHashSet<>();
    Bukkit.getPluginManager().registerEvents(this, host);
    Bukkit.getOnlinePlayers().forEach(
        player -> hologramViews.put(player, new HologramView(player))); // Handle reloads
    Bukkit.getScheduler().runTaskTimer(host, () -> {
      if (movingHolograms.isEmpty()) {
        return;
      }
      final Set<MovingHologram> removers = Sets.newHashSet();
      for (final MovingHologram moving : movingHolograms) {
        if (moving.isAlive()) {
          moving.onTick();
        } else {
          removers.add(moving);
        }
      }
      for (final MovingHologram remov : removers) {
        runOutMovingHologram(remov);
      }
    }, 1L, 1L);
  }

  private final IHologramFactory factory;
  private final Table<String, Long, Map<Location, AbstractHologram>> loadedHolograms;
  private final Map<Player, HologramView> hologramViews;
  private final Map<Integer, AbstractHologram> entityIDMappings;
  private final Map<AbstractHologram, Set<Integer>> entityIDinverseMappings;
  private final Set<MovingHologram> movingHolograms;
  private final Map<UUID, AbstractHologram> hologramIDMap;

  public MovingHologram createMovingHologram(final Location location, final Vector direction, final int ticksAllive) {
    final MovingHologram moving = new MovingHologram(createHologram(location), direction,
        ticksAllive);
    movingHolograms.add(moving);
    return moving;
  }

  public AbstractHologram getHologram(UUID holoID) {
    return this.hologramIDMap.get(holoID);
  }

  protected void runOutMovingHologram(final MovingHologram moving) {
    removeHologram(moving.getHologram());
    movingHolograms.remove(moving);
  }

  protected void onInteract(final Player player, final int entityID) {
    if (!entityIDMappings.containsKey(entityID)) {
      return;
    }
    getRelativeLine(player, getHologramFromEntityID(entityID)).onClick(player);
  }

  public Set<Player> getViewing(final AbstractHologram hologram) {
    final Set<Player> viewers = Sets.newHashSet();
    for (final Player player : Bukkit.getOnlinePlayers()) {
      if (isViewing(player, hologram)) {
        viewers.add(player);
      }
    }
    return viewers;
  }

  private IHologramLine<?> getRelativeLine(final Player player, final AbstractHologram hologram) {
    final Vector playerDirection = player.getEyeLocation().getDirection();
    final Vector playerLocation = player.getEyeLocation().toVector();

    final Map<Vector, IHologramLine<?>> hologramReferences = Maps.newHashMap();

    for (int index = 0; index < hologram.getSize(); index++) {
      final IHologramLine<?> line = hologram.getHologramLine(index);
      final Vector lineVector = line.getLocation().toVector().subtract(playerLocation)
          .add(new Vector(0, 0.48, 0));
      hologramReferences.put(lineVector, line);
    }

    return hologramReferences.get(hologramReferences.keySet().stream().min(
        (vec1, vec2) -> Float.compare(vec1.angle(playerDirection), vec2.angle(playerDirection)))
        .get());
  }

  private AbstractHologram getHologramFromEntityID(final int id) {
    return entityIDMappings.get(id);
  }

  protected void setClickableIdentifier(final Set<Integer> ids, final AbstractHologram hologram) {
    entityIDinverseMappings.put(hologram, ids);
    for (final Integer id : ids) {
      entityIDMappings.put(id, hologram);
    }
  }

  @EventHandler
  public void onChunkReceiving(final PlayerReceiveChunkEvent event) {
    final String worldName = event.getPlayer().getWorld().getName();
    final Long chunkID = event.getChunkKey();
    final Player player = event.getPlayer();

    if (!loadedHolograms.contains(worldName, chunkID)) {
      return;
    }

    for (final AbstractHologram hologram : loadedHolograms.get(worldName, chunkID).values()) {
      final HologramView view = getViewOf(player);
      if (hologram.isViableViewer(player)) {
        view.addHologram(hologram);
      }
    }
  }

  @EventHandler
  public void onChunkUnload(final PlayerUnloadsChunkEvent event) {
    final String worldName = event.getPlayer().getWorld().getName();
    final Long chunkID = event.getChunkKey();
    final Player player = event.getPlayer();

    if (!loadedHolograms.contains(worldName, chunkID)) {
      return;
    }

    for (final AbstractHologram hologram : loadedHolograms.get(worldName, chunkID).values()) {
      final HologramView view = getViewOf(player);
      if (view.isViewing(hologram)) {
        view.removeHologram(hologram);
      }
    }
  }

  public AbstractHologram createHologram(final Location location, UUID uid) {
    return createHologram(location, (player) -> true, uid);
  }

  public AbstractHologram createHologram(final Location location) {
    return createHologram(location, UUID.randomUUID());
  }

  public AbstractHologram createHologram(final Location location, final Predicate<Player> viewFilter, UUID holoID) {

    final World world = location.getWorld();
    final Long chunkID = UtilChunk.getChunkKey(location);
    final Map<Location, AbstractHologram> chunkHolograms;
    if (!loadedHolograms.contains(world.getName(), chunkID)) {
      loadedHolograms.put(world.getName(), chunkID, Maps.newHashMap());
    }

    chunkHolograms = loadedHolograms.get(world.getName(), chunkID);

    final AbstractHologram hologram = factory.supplyHologram(location, viewFilter, this, holoID);

    chunkHolograms.put(location, hologram);

    for (final Player player : location.getWorld().getPlayers()) {
      if (ChunkTracker.getChunkViews(player).contains(chunkID)) {
        final HologramView view = getViewOf(player);
        if (hologram.isViableViewer(player)) {
          view.addHologram(hologram);
        }
      }
    }

    this.hologramIDMap.put(holoID, hologram);
    return hologram;
  }

  public void removeHologram(final AbstractHologram hologram) {
    for (final HologramView view : hologramViews.values()) {
      if (view.isViewing(hologram)) {
        view.removeHologram(hologram);
      }
    }
    final Location holoLoc = hologram.getBaseLocation();
    final World holoWorld = holoLoc.getWorld();
    final Long chunkID = UtilChunk.getChunkKey(holoLoc);
    final Map<Location, AbstractHologram> chunkMap = loadedHolograms.get(holoWorld.getName(), chunkID);
    chunkMap.remove(holoLoc);
    if (entityIDinverseMappings.containsKey(hologram)) {
      for (final Integer id : entityIDinverseMappings.get(hologram)) {
        entityIDMappings.remove(id);
      }
      entityIDinverseMappings.remove(hologram);
    }
    //TODO if memory leaks. Fix
//    if (chunkMap.isEmpty()) {
//      loadedHolograms.remove(holoWorld.getName(), chunkID);
//    }
    this.hologramIDMap.remove(hologram.getHoloID());
  }

  public boolean isViewing(final Player player, final AbstractHologram hologram) {
    return hologramViews.get(player).isViewing(hologram);
  }

  public HologramView getViewOf(final Player player) {
    return hologramViews.get(player);
  }

  @EventHandler
  public void handleJoin(final PlayerJoinEvent event) {
    hologramViews.put(event.getPlayer(), new HologramView(event.getPlayer()));
  }

  @EventHandler
  public void handleQuit(final PlayerQuitEvent event) {
    hologramViews.remove(event.getPlayer());
  }
}