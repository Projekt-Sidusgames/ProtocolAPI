package net.crytec.libs.protocol.holograms.impl;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.crytec.libs.protocol.holograms.AbstractHologram;
import net.crytec.libs.protocol.holograms.AbstractHologramManager;
import net.crytec.libs.protocol.holograms.IHologramLine;
import net.minecraft.server.v1_16_R2.DamageSource;
import net.minecraft.server.v1_16_R2.Entity;
import net.minecraft.server.v1_16_R2.EntitySlime;
import net.minecraft.server.v1_16_R2.EntityTypes;
import net.minecraft.server.v1_16_R2.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R2.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R2.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_16_R2.PlayerConnection;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Hologram extends AbstractHologram {

  public Hologram(final Location baseLocation, final Predicate<Player> playerFilter,
      final AbstractHologramManager manager, UUID uid) {
    super(baseLocation, playerFilter, manager, uid);
    clickableEntitys = Sets.newHashSet();
  }

  private final Set<ClickableEntity> clickableEntitys;

  @Override
  public void appendTextLine(final String text) {
    appendLine(
        new HologramTextLine(getBaseLocation().clone().subtract(0, 0.25 * getSize(), 0),
            text, this));
    if (clickable && clickableEntitys.size() < getSize() / 4) {
      fillClickableEntities();
    }
  }

  @Override
  public void appendItemLine(final ItemStack item) {
    appendLine(
        new HologramItemLine(getBaseLocation().clone().subtract(0, 0.25 * getSize(), 0),
            item, this));
    if (clickable && clickableEntitys.size() < getSize() / 4) {
      fillClickableEntities();
    }
  }

  private void fillClickableEntities() {
    final Set<Player> viewers = getViewers();
    for (final Player player : viewers) {
      hideClickableEntities(player);
    }
    int dsize = getSize() / 4;
    dsize = Math.max(dsize, 1);
    while (clickableEntitys.size() < dsize) {
      clickableEntitys.add(new ClickableEntity(getBaseLocation().clone().subtract(0, clickableEntitys.size() * -1.5, 0)));
    }
    for (final Player player : viewers) {
      showClickableEntities(player);
    }
    registerClickableEntities();
  }

  @Override
  public void setClickable() {
    if (clickable) {
      return;
    }
    clickable = true;
    fillClickableEntities();
  }

  @Override
  protected void showClickableEntities(final Player player) {
    for (final ClickableEntity clickable : clickableEntitys) {
      clickable.sendSpawnPacket(player);
    }
  }

  @Override
  protected void hideClickableEntities(final Player player) {
    for (final ClickableEntity clickable : clickableEntitys) {
      clickable.sendDespawnPacket(player);
    }
  }

  private static final class ClickableEntity extends EntitySlime {

    private ClickableEntity(final Location location) {
      super(EntityTypes.SLIME, ((CraftWorld) location.getWorld()).getHandle());
      setPosition(location.getX(), location.getY(), location.getZ());
      setInvisible(true);
      setSize(2, true);
      setInvulnerable(true);
    }

    @Override
    public boolean damageEntity(final DamageSource damagesource, final float f) {
      return false;
    }

    @Override
    protected boolean damageEntity0(final DamageSource damagesource, final float f) {
      return false;
    }


    public void sendSpawnPacket(final Player player) {
      PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
      connection.sendPacket(new PacketPlayOutSpawnEntityLiving(this));
      connection.sendPacket(new PacketPlayOutEntityMetadata(this.getId(), this.datawatcher, true));
    }

    public void sendDespawnPacket(final Player player) {
      ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(getId()));
    }
  }

  @Override
  protected Set<Integer> getClickableEntityIds() {
    return clickableEntitys.stream().map(Entity::getId).collect(Collectors.toSet());
  }

  @Override
  protected void moveHologram(final Vector direction) {
    for (final Player viewer : getViewers()) {
      for (final IHologramLine<?> line : lines) {
        line.sendMove(viewer, direction);
      }
    }
  }
}
