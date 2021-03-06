package net.crytec.libs.protocol;

import java.util.logging.Logger;
import lombok.Getter;
import net.crytec.libs.protocol.tracking.ChunkTracker;
import net.crytec.libs.protocol.tracking.EntityTracker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ProtocolAPI {

  @Getter
  private static Logger pluginLogger;

  public ProtocolAPI(final JavaPlugin host) {
    pluginLogger = host.getLogger();
    final Plugin plugin = Bukkit.getPluginManager().getPlugin("ProtocolLib");
    if (plugin == null) {
      throw new UnsupportedOperationException("Unable to initialize ProtocolAPI - ProtocolLib is not installed on this server.");
    }

    Bukkit.getPluginManager().registerEvents(new ChunkTracker(host), host);
    Bukkit.getPluginManager().registerEvents(new EntityTracker(host), host);
  }

}