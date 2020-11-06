package net.crytec.libs.protocol.holograms.impl;

import net.crytec.libs.protocol.holograms.AbstractHologramItemLine;
import net.crytec.libs.protocol.util.WrapperPlayServerMount;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityItem;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntity.PacketPlayOutRelEntityMove;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import net.minecraft.server.v1_16_R3.Vec3D;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class HologramItemLine extends AbstractHologramItemLine {

  public HologramItemLine(final Location location, final ItemStack itemStack, final Hologram hologram) {
    super(location, itemStack, hologram);
    itemEntity = new ItemEntity(location, itemStack);
    itemVehicle = new ItemVehicle(location.clone().add(0, -0.1, 0));
    packets = new Packet<?>[6];
    packets[0] = itemEntity.getSpawnPacket();
    packets[1] = itemEntity.getDespawnPacket();
    packets[2] = itemEntity.getUpdatePacket();
    packets[3] = itemVehicle.getSpawnPacket();
    packets[4] = itemVehicle.getDespawnPacket();
    packets[5] = itemVehicle.getHidePacket();
    mountPacket = itemVehicle.getMountPacket(itemEntity);
  }

  private final ItemEntity itemEntity;
  private final ItemVehicle itemVehicle;
  private final Packet<?>[] packets;
  private WrapperPlayServerMount mountPacket;

  @Override
  public void showTo(final Player player) {
    final PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
    connection.sendPacket(packets[0]);
    connection.sendPacket(packets[3]);
    connection.sendPacket(packets[2]);
    connection.sendPacket(packets[5]);
    mountPacket.sendPacket(player);
  }

  @Override
  public void hideFrom(final Player player) {
    final PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
    connection.sendPacket(packets[1]);
    connection.sendPacket(packets[4]);
  }

  @Override
  public void update(final ItemStack newValue) {
    itemEntity.setItemStack(CraftItemStack.asNMSCopy(newValue));
    packets[0] = itemEntity.getSpawnPacket();
    packets[1] = itemEntity.getDespawnPacket();
    packets[2] = itemEntity.getUpdatePacket();
    mountPacket = itemVehicle.getMountPacket(itemEntity);

    for (final Player player : getHostingHologram().getViewers()) {
      final PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
      connection.sendPacket(packets[2]);
      connection.sendPacket(packets[5]);
    }
  }

  private final class ItemVehicle extends EntityArmorStand {

    public ItemVehicle(final Location location) {
      super(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY() + 0.2,
          location.getZ());
      setMarker(true);
      setInvisible(true);
      setCustomNameVisible(false);
    }

    public PacketPlayOutSpawnEntity getSpawnPacket() {
      return new PacketPlayOutSpawnEntity(this);
    }

    public PacketPlayOutEntityMetadata getHidePacket() {
      return new PacketPlayOutEntityMetadata(getId(), getDataWatcher(), true);
    }

    public PacketPlayOutEntityDestroy getDespawnPacket() {
      return new PacketPlayOutEntityDestroy(getId());
    }

    public WrapperPlayServerMount getMountPacket(final EntityItem item) {
      final WrapperPlayServerMount packet = new WrapperPlayServerMount();

      packet.setEntityID(getId());
      packet.setPassengerIds(new int[]{item.getId()});

      return packet;
    }

  }

  private final class ItemEntity extends EntityItem {

    private ItemEntity(final Location location, final ItemStack item) {
      super(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(),
          location.getZ(), CraftItemStack.asNMSCopy(item));
      setInvulnerable(true);
      setNoGravity(true);
      setMot(new Vec3D(0, 0, 0));
      velocityChanged = true;
    }

    @Override
    public void tick() {

    }

    public PacketPlayOutSpawnEntity getSpawnPacket() {
      return new PacketPlayOutSpawnEntity(this);
    }

    public PacketPlayOutEntityDestroy getDespawnPacket() {
      return new PacketPlayOutEntityDestroy(getId());
    }

    public PacketPlayOutEntityMetadata getUpdatePacket() {
      return new PacketPlayOutEntityMetadata(getId(), getDataWatcher(), true);
    }

  }

  @Override
  public void sendMove(final Player player, final Vector direction) {
    final PacketPlayOutRelEntityMove movePacket = new PacketPlayOutRelEntityMove(itemEntity.getId(),
        (short) (direction.getX() * 4096), (short) (direction.getY() * 4096),
        (short) (direction.getZ() * 4096), false);
    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(movePacket);
  }

}
