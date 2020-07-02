package net.crytec.libs.protocol.tracking;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import net.crytec.libs.protocol.events.PlayerReceiveEntityEvent;
import net.crytec.libs.protocol.events.PlayerUnloadsEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class EntityTracker implements Listener {

  private static final Map<Player, Set<Integer>> playerViews = Maps.newHashMap();


  public EntityTracker(final JavaPlugin host) {
    Bukkit.getOnlinePlayers().forEach(player -> EntityTracker.playerViews.put(player, Sets.newHashSet()));

    ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(host, PacketType.Play.Server.SPAWN_ENTITY_LIVING) {

      @Override
      public void onPacketSending(final PacketEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY_LIVING) {
          if (event.getPacket().getMeta("phoenix-ignore").isPresent()) {
            return;
          }

          final PacketContainer packet = event.getPacket();

          final Player viewer = event.getPlayer();
          final int entityID = packet.getIntegers().read(0);

          final PlayerReceiveEntityEvent recieveEvent = new PlayerReceiveEntityEvent(viewer, entityID);
          Bukkit.getScheduler().runTask(host, () -> Bukkit.getPluginManager().callEvent(recieveEvent));
        }
      }
    });

    ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(host, PacketType.Play.Server.ENTITY_DESTROY) {

      @Override
      public void onPacketSending(final PacketEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_DESTROY) {
          if (event.getPacket().getMeta("phoenix-ignore").isPresent()) {
            return;
          }

          final PacketContainer packet = event.getPacket();

          final int[] entityIDs = packet.getIntegerArrays().getValues().get(0);
          final Player viewer = event.getPlayer();

          final PlayerUnloadsEntityEvent unloadEvent = new PlayerUnloadsEntityEvent(viewer, entityIDs);
          Bukkit.getScheduler().runTask(host, () -> Bukkit.getPluginManager().callEvent(unloadEvent));
        }
      }
    });
  }

  @EventHandler
  public void onJoin(final PlayerJoinEvent event) {
    EntityTracker.playerViews.put(event.getPlayer(), Sets.newHashSet());
  }

  @EventHandler
  public void onQuit(final PlayerQuitEvent event) {
    EntityTracker.playerViews.remove(event.getPlayer());
  }

  @EventHandler
  public void onEntityShowing(final PlayerReceiveEntityEvent event) {
    EntityTracker.playerViews.get(event.getPlayer()).add(event.getEntityID());
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onEntityHiding(final PlayerUnloadsEntityEvent event) {
    final Set<Integer> ints = EntityTracker.playerViews.get(event.getPlayer());
    for (final int id : event.getEntityIDs()) {
      ints.remove(id);
    }
  }

  public static Set<Integer> getEntityViewOf(final Player player) {
    return playerViews.get(player);
  }
}