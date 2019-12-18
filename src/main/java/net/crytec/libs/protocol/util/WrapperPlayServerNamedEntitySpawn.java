/**
 * PacketWrapper - ProtocolLib wrappers for Minecraft packets Copyright (C) dmulloy2 <http://dmulloy2.net> Copyright (C) Kristian S. Strangeland
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.crytec.libs.protocol.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import java.util.UUID;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class WrapperPlayServerNamedEntitySpawn extends AbstractPacket {

  public static final PacketType TYPE =
      PacketType.Play.Server.NAMED_ENTITY_SPAWN;

  public WrapperPlayServerNamedEntitySpawn() {
    super(new PacketContainer(TYPE), TYPE);
    this.handle.getModifier().writeDefaults();
  }

  public WrapperPlayServerNamedEntitySpawn(final PacketContainer packet) {
    super(packet, TYPE);
  }

  /**
   * Retrieve Entity ID.
   * <p>
   * Notes: entity's ID
   *
   * @return The current Entity ID
   */
  public int getEntityID() {
    return this.handle.getIntegers().read(0);
  }

  /**
   * Set Entity ID.
   *
   * @param value - new value.
   */
  public void setEntityID(final int value) {
    this.handle.getIntegers().write(0, value);
  }

  /**
   * Retrieve the entity of the painting that will be spawned.
   *
   * @param world - the current world of the entity.
   * @return The spawned entity.
   */
  public Entity getEntity(final World world) {
    return this.handle.getEntityModifier(world).read(0);
  }

  /**
   * Retrieve the entity of the painting that will be spawned.
   *
   * @param event - the packet event.
   * @return The spawned entity.
   */
  public Entity getEntity(final PacketEvent event) {
    return this.getEntity(event.getPlayer().getWorld());
  }

  /**
   * Retrieve Player UUID.
   * <p>
   * Notes: player's UUID
   *
   * @return The current Player UUID
   */
  public UUID getPlayerUUID() {
    return this.handle.getUUIDs().read(0);
  }

  /**
   * Set Player UUID.
   *
   * @param value - new value.
   */
  public void setPlayerUUID(final UUID value) {
    this.handle.getUUIDs().write(0, value);
  }

  /**
   * Retrieve the position of the spawned entity as a vector.
   *
   * @return The position as a vector.
   */
  public Vector getPosition() {
    return new Vector(this.getX(), this.getY(), this.getZ());
  }

  /**
   * Set the position of the spawned entity using a vector.
   *
   * @param position - the new position.
   */
  public void setPosition(final Vector position) {
    this.setX(position.getX());
    this.setY(position.getY());
    this.setZ(position.getZ());
  }

  public double getX() {
    return this.handle.getDoubles().read(0);
  }

  public void setX(final double value) {
    this.handle.getDoubles().write(0, value);
  }

  public double getY() {
    return this.handle.getDoubles().read(1);
  }

  public void setY(final double value) {
    this.handle.getDoubles().write(1, value);
  }

  public double getZ() {
    return this.handle.getDoubles().read(2);
  }

  public void setZ(final double value) {
    this.handle.getDoubles().write(2, value);
  }

  /**
   * Retrieve the yaw of the spawned entity.
   *
   * @return The current Yaw
   */
  public float getYaw() {
    return (this.handle.getBytes().read(0) * 360.F) / 256.0F;
  }

  /**
   * Set the yaw of the spawned entity.
   *
   * @param value - new yaw.
   */
  public void setYaw(final float value) {
    this.handle.getBytes().write(0, (byte) (value * 256.0F / 360.0F));
  }

  /**
   * Retrieve the pitch of the spawned entity.
   *
   * @return The current pitch
   */
  public float getPitch() {
    return (this.handle.getBytes().read(1) * 360.F) / 256.0F;
  }

  /**
   * Set the pitch of the spawned entity.
   *
   * @param value - new pitch.
   */
  public void setPitch(final float value) {
    this.handle.getBytes().write(1, (byte) (value * 256.0F / 360.0F));
  }

  /**
   * Retrieve Metadata.
   * <p>
   * Notes: the client will crash if no metadata is sent
   *
   * @return The current Metadata
   */
  public WrappedDataWatcher getMetadata() {
    return this.handle.getDataWatcherModifier().read(0);
  }

  /**
   * Set Metadata.
   *
   * @param value - new value.
   */
  public void setMetadata(final WrappedDataWatcher value) {
    this.handle.getDataWatcherModifier().write(0, value);
  }
}