package net.crytec.libs.protocol.npc.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.crytec.libs.protocol.npc.NPC;
import net.crytec.libs.protocol.tracking.ChunkTracker;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class NpcManager {

  private static final ArrayList<NPC<?>> EMPTY_LIST = Lists.newArrayList();
  // Row | Column | Value
  private final Map<UUID, Map<Long, Set<NPC<?>>>> npcs = Maps.newHashMap();
  private final Map<Integer, NPC<?>> entityIDMappings = Maps.newHashMap();

  public Collection<NPC<?>> getNPCsInChunk(final UUID worldID, final long chunkKey) {
    final Map<Long, Set<NPC<?>>> chunkMap = this.npcs.get(worldID);
    if (chunkMap == null) {
      return EMPTY_LIST;
    }
    final Set<NPC<?>> npcSet = chunkMap.get(chunkKey);
    if (npcSet == null) {
      return EMPTY_LIST;
    }
    return npcSet;
  }

  public boolean isNPC(final int entityID) {
    return this.entityIDMappings.containsKey(entityID);
  }

  public NPC<?> getNPC(final int entityID) {
    return this.entityIDMappings.get(entityID);
  }

  public void spawnNPC(final NPC<?> npc) {

    final long chunkKey = this.getChunkKey(npc.getLocation().getChunk());
    final UUID worldID = npc.getLocation().getWorld().getUID();

    if (!this.npcs.containsKey(worldID)) {
      this.npcs.put(worldID, Maps.newHashMap());
    }
    final Map<Long, Set<NPC<?>>> chunkMap = this.npcs.get(worldID);
    if (!chunkMap.containsKey(chunkKey)) {
      chunkMap.put(chunkKey, Sets.newHashSet());
    }
    final Set<NPC<?>> chunkEntities = chunkMap.get(chunkKey);
    chunkEntities.add(npc);

    this.entityIDMappings.put(npc.getFakeEntity().getId(), npc);

    for (final Player player : npc.getLocation().getWorld().getPlayers()) {
      if (!ChunkTracker.isChunkInView(player, chunkKey)) {
        continue;
      }
      npc.spawnFor(player);
    }
  }

  public void removeNPC(final NPC<?> npc) {
    final UUID worldID = npc.getLocation().getWorld().getUID();
    final long chunkKey = this.getChunkKey(npc.getLocation());
    final Map<Long, Set<NPC<?>>> chunkMap = this.npcs.get(worldID);
    final Set<NPC<?>> npcSet = chunkMap.get(chunkKey);
    npcSet.remove(npc);

    for (final Player player : npc.getLocation().getWorld().getPlayers()) {
      if (ChunkTracker.isChunkInView(player, chunkKey)) {
        npc.despawnFor(player);
      }
    }

    if (npcSet.isEmpty()) {
      chunkMap.remove(chunkKey);
    }
    if (chunkMap.isEmpty()) {
      this.npcs.remove(worldID);
    }
  }

  public long getChunkKey(final int x, final int z) {
    return (long) x & 0xffffffffL | ((long) z & 0xffffffffL) << 32;
  }

  public long getChunkKey(final Location location) {
    return this.getChunkKey(location.getBlockX() >> 4, location.getBlockZ() >> 4);
  }

  public long getChunkKey(final Chunk chunk) {
    return (long) chunk.getX() & 0xffffffffL | ((long) chunk.getZ() & 0xffffffffL) << 32;
  }
}