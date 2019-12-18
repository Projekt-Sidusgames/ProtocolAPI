package net.crytec.libs.protocol.tracking;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.crytec.libs.protocol.events.PlayerReceiveChunkEvent;
import net.crytec.libs.protocol.events.PlayerUnloadsChunkEvent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ChunkTracker implements Listener {

  private static final Map<Player, LongSet> chunkViews = Maps.newHashMap();

  public ChunkTracker(final JavaPlugin host) {
    Bukkit.getOnlinePlayers().forEach(player -> chunkViews.put(player, new LongOpenHashSet()));

    ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(host, PacketType.Play.Server.MAP_CHUNK) {

      @Override
      public void onPacketSending(final PacketEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.MAP_CHUNK) {
          if (event.getPacket().getMeta("phoenix-ignore").isPresent()) {
            return;
          }

          Bukkit.getScheduler().runTask(host, () -> {
            final PacketContainer packet = event.getPacket();
            final List<Integer> coords = packet.getIntegers().getValues();
            final Player player = event.getPlayer();
            final long chunkKey = (long) coords.get(0) & 0xffffffffL | ((long) coords.get(1) & 0xffffffffL) << 32;

            final PlayerReceiveChunkEvent e = new PlayerReceiveChunkEvent(player, chunkKey);
            Bukkit.getPluginManager().callEvent(e);
          });

        }
      }
    });

    ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(host, PacketType.Play.Server.UNLOAD_CHUNK) {

      @Override
      public void onPacketSending(final PacketEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.UNLOAD_CHUNK) {
          if (event.getPacket().getMeta("phoenix-ignore").isPresent()) {
            return;
          }

          Bukkit.getScheduler().runTask(host, () -> {
            final PacketContainer packet = event.getPacket();
            final List<Integer> coords = packet.getIntegers().getValues();
            final Player player = event.getPlayer();
            final long chunkKey = (long) coords.get(0) & 0xffffffffL | ((long) coords.get(1) & 0xffffffffL) << 32;

            final PlayerUnloadsChunkEvent e = new PlayerUnloadsChunkEvent(player, chunkKey);
            Bukkit.getPluginManager().callEvent(e);
          });
        }
      }
    });
  }

  @EventHandler
  public void onChunkReceive(final PlayerReceiveChunkEvent event) {
    chunkViews.get(event.getPlayer()).add(event.getChunkKey());
  }

  @EventHandler
  public void onChunkReceive(final PlayerUnloadsChunkEvent event) {
    chunkViews.get(event.getPlayer()).remove(event.getChunkKey());
  }

  @EventHandler(priority = EventPriority.LOW)
  public void onJoin(final PlayerJoinEvent event) {
    chunkViews.put(event.getPlayer(), new LongOpenHashSet());
  }

  @EventHandler
  public void onQuit(final PlayerQuitEvent event) {
    chunkViews.remove(event.getPlayer());
  }

  public static LongSet getChunkViews(final Player player) {
    return chunkViews.get(player);
  }

  public static boolean isChunkInView(final Player player, final long chunkKey) {
    return chunkViews.get(player).contains(chunkKey);
  }

  public static boolean isChunkInView(final Player player, final Chunk chunk) {
    return isChunkInView(player, getChunkKey(chunk));
  }

  private static long getChunkKey(final int x, final int z) {
    return (long) x & 0xffffffffL | ((long) z & 0xffffffffL) << 32;
  }

  private static long getChunkKey(final Chunk chunk) {
    return (long) chunk.getX() & 0xffffffffL | ((long) chunk.getZ() & 0xffffffffL) << 32;
  }

  public static Set<Long> getChunkViewOf(final Player player) {
    return chunkViews.get(player);
  }

}