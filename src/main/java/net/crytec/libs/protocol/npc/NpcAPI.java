package net.crytec.libs.protocol.npc;

import com.comphenix.protocol.ProtocolLibrary;
import net.crytec.libs.protocol.npc.listener.ChunkListener;
import net.crytec.libs.protocol.npc.listener.packetlistener.NPCInteractListener;
import net.crytec.libs.protocol.npc.manager.NpcManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftChunk;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class NpcAPI {

  protected static JavaPlugin host;

  private final NpcManager manager;

  public NpcAPI(final JavaPlugin host) {
    NpcAPI.host = host;
    final Plugin plugin = Bukkit.getPluginManager().getPlugin("ProtocolLib");
    if (plugin == null) {
      throw new UnsupportedOperationException("Unable to initialize ProtocolAPI - ProtocolLib is not installed on this server.");
    }
    this.manager = new NpcManager();
    Bukkit.getPluginManager().registerEvents(new ChunkListener(this.manager), host);

    ProtocolLibrary.getProtocolManager().addPacketListener(new NPCInteractListener(host, this.manager));
    host.getLogger().info("Initialized NPC API");

  }

  public NpcManager getNPCManager() {
    return this.manager;
  }

}
