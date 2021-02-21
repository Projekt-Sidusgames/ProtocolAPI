package net.crytec.libs.protocol.npc;

import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.List;
import java.util.UUID;
import net.crytec.libs.commons.utils.UtilChunk;
import net.crytec.libs.protocol.tracking.ChunkTracker;
import net.crytec.libs.protocol.util.WrapperPlayServerNamedEntitySpawn;
import net.crytec.libs.protocol.util.WrapperPlayServerPlayerInfo;
import net.minecraft.server.v1_16_R3.ChatMessage;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntity.PacketPlayOutEntityLook;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_16_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import net.minecraft.server.v1_16_R3.PlayerInteractManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public abstract class NPC<T extends EntityLiving> {

  private Location location;
  private Entity fakeEntity;

  private Packet<?> spawnPacket;
  private Packet<?> metaDataPacket;

  public NPC(final Location location) {
    this.location = location;
    this.createFakeEntity();
  }

  public T getFakeEntity() {
    return (T) this.fakeEntity;
  }

  protected final void createFakeEntity() {
    final net.minecraft.server.v1_16_R3.WorldServer nmsWorld = ((CraftWorld) this.location.getWorld()).getHandle();

    if (this.getType() == EntityTypes.PLAYER) {

      final GameProfile profile = new GameProfile(UUID.randomUUID(), "API_NPC");
      this.fakeEntity = new EntityPlayer(nmsWorld.getMinecraftServer(), nmsWorld, profile, new PlayerInteractManager(nmsWorld));
      ((EntityPlayer) this.fakeEntity).listName = new ChatMessage("placeholder");
    } else {
      this.fakeEntity = this.getType().a(nmsWorld);
      if (this.fakeEntity == null) {
        throw new IllegalArgumentException("Failed to create FakeEntity. Type is null");
      }
    }

    this.fakeEntity.setPosition(this.location.getX(), this.location.getY(), this.location.getZ());
    this.fakeEntity.setHeadRotation(this.location.getYaw());
    this.fakeEntity.yaw = this.location.getYaw();
    this.fakeEntity.pitch = this.getLocation().getPitch();

    if (this.fakeEntity instanceof EntityPlayer) {
      this.spawnPacket = new PacketPlayOutNamedEntitySpawn((EntityPlayer) this.fakeEntity);
    } else if (this.fakeEntity instanceof EntityLiving) {
      this.spawnPacket = new PacketPlayOutSpawnEntityLiving((EntityLiving) this.fakeEntity);
    } else {
      this.spawnPacket = new PacketPlayOutSpawnEntity(this.fakeEntity);
    }

    this.metaDataPacket = new PacketPlayOutEntityMetadata(this.fakeEntity.getId(), this.fakeEntity.getDataWatcher(), true);

  }

  public final Location getLocation() {
    return this.location;
  }

  public final void teleport(final Location location) {
    this.fakeEntity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    this.location = location;

    final PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(this.fakeEntity);
    this.sendPacketNearby(packet);
  }

  protected abstract EntityTypes<?> getType();

  public final Packet<?> getSpawnPacket() {
    return this.spawnPacket;
  }

  public final Packet<?> getDespawnpacket() {
    return new PacketPlayOutEntityDestroy(this.getFakeEntity().getId());
  }

  public final Packet<?> getMetaDataPacket() {
    return this.metaDataPacket;
  }

  private PacketPlayOutEntityLook getEntityLookPacket() {
    return new PacketPlayOutEntityLook(this.fakeEntity.getId(), (byte) (this.fakeEntity.yaw * 256F / 360F), (byte) (this.fakeEntity.pitch * 256F / 360F), true);
  }

  public final void spawnFor(final Player player) {
    final PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

    if (this.fakeEntity.getEntityType() == EntityTypes.PLAYER) {

      final WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();
      playerInfo.setAction(PlayerInfoAction.ADD_PLAYER);

      final WrappedGameProfile profile = new WrappedGameProfile(UUID.fromString("8f1e1493-3ef8-4169-9b6e-ee7551475eb1"), "Gestankbratwurst");

      final PlayerInfoData data = new PlayerInfoData(profile, 1, NativeGameMode.CREATIVE, WrappedChatComponent.fromText("Test NPC"));

      final List<PlayerInfoData> dataList = Lists.newArrayList();
      dataList.add(data);
      playerInfo.setData(dataList);
      playerInfo.broadcastPacket();

      final WrapperPlayServerNamedEntitySpawn entity = new WrapperPlayServerNamedEntitySpawn();

      entity.setEntityID(this.fakeEntity.getId());
      entity.setPlayerUUID(profile.getUUID());
      entity.setPosition(this.location.toVector());
      entity.setYaw(this.location.getYaw());
      entity.setPitch(this.location.getPitch());

      final WrapperPlayServerPlayerInfo hideTab = new WrapperPlayServerPlayerInfo();
      hideTab.setAction(PlayerInfoAction.REMOVE_PLAYER);

      Bukkit.getScheduler().runTaskLater(NpcAPI.host, entity::broadcastPacket, 2L);
      Bukkit.getScheduler().runTaskLater(NpcAPI.host, () -> connection.sendPacket(this.getMetaDataPacket()), 3L);
      Bukkit.getScheduler().runTaskLater(NpcAPI.host, hideTab::broadcastPacket, 5L);

      return;
    }

    connection.sendPacket(this.getSpawnPacket());
    updateMetadata();
    Bukkit.getScheduler().runTaskLater(NpcAPI.host, () -> connection.sendPacket(this.getMetaDataPacket()), 2L);
  }

  protected void updateMetadata() {
    this.metaDataPacket = new PacketPlayOutEntityMetadata(this.fakeEntity.getId(), this.fakeEntity.getDataWatcher(), true);
  }

  public final void look(final float yaw, final float pitch) {
    final PacketPlayOutEntityLook look = new PacketPlayOutEntityLook(this.fakeEntity.getId(), (byte) (yaw * 256F / 360F), (byte) (pitch * 256F / 360F), true);

    this.fakeEntity.yaw = yaw;
    this.fakeEntity.pitch = pitch;

    this.location.setYaw(yaw);
    this.location.setPitch(pitch);
    this.sendPacketNearby(look);
  }

  public final void despawnFor(final Player player) {
    final PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

    if (this.fakeEntity instanceof EntityPlayer) {
      connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, (EntityPlayer) this.fakeEntity));
    }

    connection.sendPacket(this.getDespawnpacket());
  }

  public void onPlayerInteraction(final Player player) {
  }

  public void setDisplayname(final String displayname) {
    this.fakeEntity.setCustomName(new ChatMessage(displayname));
    this.fakeEntity.setCustomNameVisible(true);

    final PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(this.fakeEntity.getId(), this.fakeEntity.getDataWatcher(), true);
    this.sendPacketNearby(metadata);
  }

  public void sendPacketNearby(final Packet<?>... packets) {
    final Location location = this.getLocation();
    if (UtilChunk.isChunkLoaded(location)) {
      for (final Player player : location.getWorld().getPlayers()) {
        if (ChunkTracker.isChunkInView(player, location.getChunk())) {
          final PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
          for (final Packet<?> packet : packets) {
            connection.sendPacket(packet);
          }
        }
      }
    }
  }

}
