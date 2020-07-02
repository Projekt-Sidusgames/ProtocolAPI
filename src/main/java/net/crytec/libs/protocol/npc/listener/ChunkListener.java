package net.crytec.libs.protocol.npc.listener;

import net.crytec.libs.protocol.events.PlayerReceiveChunkEvent;
import net.crytec.libs.protocol.events.PlayerUnloadsChunkEvent;
import net.crytec.libs.protocol.npc.NPC;
import net.crytec.libs.protocol.npc.manager.NpcManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChunkListener implements Listener {

  private final NpcManager manager;

  public ChunkListener(final NpcManager manager) {
    this.manager = manager;
  }

  @EventHandler
  public void onChunkUnload(final PlayerUnloadsChunkEvent event) {
    for (final NPC<?> npc : this.manager.getNPCsInChunk(event.getPlayer().getWorld().getUID(), event.getChunkKey())) {
      npc.despawnFor(event.getPlayer());
    }
  }

  @EventHandler
  public void onChunkLoad(final PlayerReceiveChunkEvent event) {
    for (final NPC<?> npc : this.manager.getNPCsInChunk(event.getPlayer().getWorld().getUID(), event.getChunkKey())) {
      npc.spawnFor(event.getPlayer());
    }
  }
}