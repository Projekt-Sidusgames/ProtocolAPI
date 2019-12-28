package net.crytec.libs.protocol.holograms.impl;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import java.lang.reflect.InvocationTargetException;
import net.crytec.libs.protocol.holograms.AbstractHologramTextLine;
import net.minecraft.server.v1_15_R1.ChatMessage;
import net.minecraft.server.v1_15_R1.EntityArmorStand;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntity.PacketPlayOutRelEntityMove;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_15_R1.PacketPlayOutSpawnEntity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class HologramTextLine extends AbstractHologramTextLine {

  public HologramTextLine(final Location location, final String text, final Hologram hologram) {
    super(location, text, hologram);
    this.textEntity = new TextEntity(location, text);
  }

  private final TextEntity textEntity;

  @Override
  public void showTo(final Player player) {
    this.textEntity.sendSpawnPacket(player);
  }

  @Override
  public void hideFrom(final Player player) {
    this.textEntity.sendDespawnPacket(player);
  }

  @Override
  public void update(final String newValue) {
    this.textEntity.setCustomName(new ChatMessage(newValue));
    for (final Player player : this.getHostingHologram().getViewers()) {
      this.textEntity.updateMetadata(player);
    }
  }

  private final class TextEntity extends EntityArmorStand {

    public TextEntity(final Location location, final String line) {
      super(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(),
          location.getZ());
      this.setMarker(true);
      this.setInvulnerable(true);
      this.setInvisible(true);
      this.setCustomName(new ChatMessage(line));
      this.setCustomNameVisible(true);
    }

    public void sendSpawnPacket(final Player player) {
      ((CraftPlayer) player).getHandle().playerConnection
          .sendPacket(new PacketPlayOutSpawnEntity(this));
      this.updateMetadata(player);
    }

    public void sendDespawnPacket(final Player player) {

      ((CraftPlayer) player).getHandle().playerConnection
          .sendPacket(new PacketPlayOutEntityDestroy(this.getId()));
    }

    public void updateMetadata(final Player player) {
      final PacketContainer container = PacketContainer
          .fromPacket(new PacketPlayOutEntityMetadata(this.getId(), this.getDataWatcher(), true));
      container.setMeta("phoenix-hologram", true);
      try {
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, container);
      } catch (final InvocationTargetException e) {
        e.printStackTrace();
      }
//			((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityMetadata(this.getId(), this.getDataWatcher(), true));			
    }


  }

  @Override
  public void sendMove(final Player player, final Vector direction) {
    final PacketPlayOutRelEntityMove movePacket = new PacketPlayOutRelEntityMove(this.textEntity.getId(),
        (short) (direction.getX() * 4096), (short) (direction.getY() * 4096),
        (short) (direction.getZ() * 4096), false);
    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(movePacket);
  }

}
