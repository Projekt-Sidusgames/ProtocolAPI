package net.crytec.libs.protocol.holograms.impl;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import net.crytec.libs.protocol.holograms.AbstractHologramManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HologramManager extends AbstractHologramManager {

  public HologramManager(JavaPlugin host) {
    super(host, new HologramFactory());
    ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    this.registerClickListener(protocolManager, host);
  }

  private void registerClickListener(ProtocolManager protocolManager, JavaPlugin host) {

    protocolManager.addPacketListener(new PacketAdapter(host, PacketType.Play.Client.USE_ENTITY ) {

      private final long minDelayBetweenClicks = 200L;
      private final Map<UUID, Long> clickInterval = Maps.newHashMap();

      @Override
      public void onPacketReceiving(PacketEvent event) {
        if(event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
          UUID playerID = event.getPlayer().getUniqueId();
          if(this.clickInterval.containsKey(playerID) && System.currentTimeMillis() < this.clickInterval.get(playerID) + minDelayBetweenClicks) {
            return;
          }else {
            this.clickInterval.put(playerID, System.currentTimeMillis());
          }
          PacketContainer packet = event.getPacket();
          int entityID = packet.getIntegers().getValues().get(0);
          onInteract(event.getPlayer(), entityID);
        }
      }

    });
  }
}