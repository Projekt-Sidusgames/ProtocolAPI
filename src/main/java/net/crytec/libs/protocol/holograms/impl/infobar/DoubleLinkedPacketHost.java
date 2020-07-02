package net.crytec.libs.protocol.holograms.impl.infobar;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.base.Preconditions;
import java.lang.reflect.InvocationTargetException;
import net.minecraft.server.v1_16_R1.Packet;
import net.minecraft.server.v1_16_R1.PlayerConnection;
import org.bukkit.entity.Player;

public class DoubleLinkedPacketHost {

  public static DoubleLinkedPacketHost of(final Packet<?> packet) {
    return new DoubleLinkedPacketHost(packet);
  }

  public static DoubleLinkedPacketHost of(final PacketContainer packet) {
    return new DoubleLinkedPacketHost(packet);
  }

  private DoubleLinkedPacketHost(final Packet<?> packet) {
    NMSPacket = packet;
    protocolPacket = null;
    protManager = null;
    type = LinkedPacketType.NMS_PACKET;
  }

  private DoubleLinkedPacketHost(final PacketContainer packet) {
    NMSPacket = null;
    protocolPacket = packet;
    protManager = ProtocolLibrary.getProtocolManager();
    type = LinkedPacketType.PROTOCOL_PACKET;
  }

  private final ProtocolManager protManager;
  public final LinkedPacketType type;
  private final Packet<?> NMSPacket;
  private final PacketContainer protocolPacket;

  public void sendNMS(final PlayerConnection conn) {
    Preconditions.checkState(type == LinkedPacketType.NMS_PACKET);
    conn.sendPacket(NMSPacket);
  }

  public void sendProtocol(final Player player) {
    Preconditions.checkState(type == LinkedPacketType.PROTOCOL_PACKET);
    try {
      protManager.sendServerPacket(player, protocolPacket);
    } catch (final InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  public static enum LinkedPacketType{
    NMS_PACKET,
    PROTOCOL_PACKET;
  }

}
