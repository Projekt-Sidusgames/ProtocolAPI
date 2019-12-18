package net.crytec.libs.protocol.npc.manager;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
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
  private final Map<UUID, Long2ObjectMap<Set<NPC<?>>>> npcs = new Object2ObjectOpenHashMap<>();
  private final Int2ObjectMap<NPC<?>> entityIDMappings = new Int2ObjectOpenHashMap<>();

  public Collection<NPC<?>> getNPCsInChunk(UUID worldID, final long chunkKey) {
    Long2ObjectMap<Set<NPC<?>>> chunkMap = npcs.get(worldID);
    if (chunkMap == null) {
      return EMPTY_LIST;
    }
    Set<NPC<?>> npcSet = chunkMap.get(chunkKey);
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

    final long chunkKey = getChunkKey(npc.getLocation().getChunk());
    final UUID worldID = npc.getLocation().getWorld().getUID();

    if (!npcs.containsKey(worldID)) {
      npcs.put(worldID, new Long2ObjectOpenHashMap<>());
    }
    Long2ObjectMap<Set<NPC<?>>> chunkMap = this.npcs.get(worldID);
    if (!chunkMap.containsKey(chunkKey)) {
      chunkMap.put(chunkKey, new ObjectOpenHashSet<>());
    }
    Set<NPC<?>> chunkEntities = chunkMap.get(chunkKey);
    chunkEntities.add(npc);

    this.entityIDMappings.put(npc.getFakeEntity().getId(), npc);

    for (final Player player : npc.getLocation().getWorld().getPlayers()) {
      if (!ChunkTracker.isChunkInView(player, chunkKey)) {
        continue;
      }
      npc.spawnFor(player);
    }
  }

  public void removeNPC(NPC<?> npc) {
    UUID worldID = npc.getLocation().getWorld().getUID();
    long chunkKey = getChunkKey(npc.getLocation());
    Long2ObjectMap<Set<NPC<?>>> chunkMap = npcs.get(worldID);
    Set<NPC<?>> npcSet = chunkMap.get(chunkKey);
    npcSet.remove(npc);

    for (Player player : npc.getLocation().getWorld().getPlayers()) {
      if (ChunkTracker.isChunkInView(player, chunkKey)) {
        npc.despawnFor(player);
      }
    }

    if (npcSet.isEmpty()) {
      chunkMap.remove(chunkKey);
    }
    if (chunkMap.isEmpty()) {
      npcs.remove(worldID);
    }
  }

  public long getChunkKey(final int x, final int z) {
    return (long) x & 0xffffffffL | ((long) z & 0xffffffffL) << 32;
  }

  public long getChunkKey(Location location) {
    return getChunkKey(location.getBlockX() >> 4, location.getBlockZ() >> 4);
  }

  public long getChunkKey(final Chunk chunk) {
    return (long) chunk.getX() & 0xffffffffL | ((long) chunk.getZ() & 0xffffffffL) << 32;
  }
}