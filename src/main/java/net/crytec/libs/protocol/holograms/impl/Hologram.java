package net.crytec.libs.protocol.holograms.impl;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.crytec.libs.protocol.holograms.AbstractHologram;
import net.crytec.libs.protocol.holograms.AbstractHologramManager;
import net.crytec.libs.protocol.holograms.IHologramLine;
import net.minecraft.server.v1_15_R1.DamageSource;
import net.minecraft.server.v1_15_R1.EntitySlime;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_15_R1.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Hologram extends AbstractHologram {

  public Hologram(final Location baseLocation, final Predicate<Player> playerFilter,
      final AbstractHologramManager manager) {
    super(baseLocation, playerFilter, manager);
    this.clickableEntitys = Sets.newHashSet();
  }

  private final Set<ClickableEntity> clickableEntitys;

  @Override
  public void appendTextLine(final String text) {
    this.appendLine(
        new HologramTextLine(this.getBaseLocation().clone().subtract(0, 0.25 * this.getSize(), 0),
            text, this));
    if (this.clickable && this.clickableEntitys.size() < this.getSize() / 4) {
      this.fillClickableEntities();
    }
  }

  @Override
  public void appendItemLine(final ItemStack item) {
    this.appendLine(
        new HologramItemLine(this.getBaseLocation().clone().subtract(0, 0.25 * this.getSize(), 0),
            item, this));
    if (this.clickable && this.clickableEntitys.size() < this.getSize() / 4) {
      this.fillClickableEntities();
    }
  }

  private void fillClickableEntities() {
    final Set<Player> viewers = this.getViewers();
    for (final Player player : viewers) {
      this.hideClickableEntities(player);
    }
    while (this.clickableEntitys.size() < this.getSize() / 4) {
      this.clickableEntitys.add(new ClickableEntity(
          this.getBaseLocation().clone().subtract(0, this.clickableEntitys.size() * -1.5, 0)));
    }
    for (final Player player : viewers) {
      this.showClickableEntities(player);
    }
    this.registerClickableEntities();
  }

  @Override
  public void setClickable() {
    if (this.clickable) {
      return;
    }
    this.clickable = true;
    this.fillClickableEntities();
  }

  @Override
  protected void showClickableEntities(final Player player) {
    for (final ClickableEntity clickable : this.clickableEntitys) {
      clickable.sendSpawnPacket(player);
    }
  }

  @Override
  protected void hideClickableEntities(final Player player) {
    for (final ClickableEntity clickable : this.clickableEntitys) {
      clickable.sendDespawnPacket(player);
    }
  }

  private final class ClickableEntity extends EntitySlime {

    public ClickableEntity(final Location location) {
      super(EntityTypes.SLIME, ((CraftWorld) location.getWorld()).getHandle());
      this.setPosition(location.getX(), location.getY(), location.getZ());
      this.setInvisible(true);
      this.setSize(2, true);
      this.setInvulnerable(true);
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
      ((CraftPlayer) player).getHandle().playerConnection
          .sendPacket(new PacketPlayOutSpawnEntityLiving(this));
    }

    public void sendDespawnPacket(final Player player) {
      ((CraftPlayer) player).getHandle().playerConnection
          .sendPacket(new PacketPlayOutEntityDestroy(this.getId()));
    }
  }

  @Override
  protected Set<Integer> getClickableEntityIds() {
    return this.clickableEntitys.stream().map(e -> e.getId()).collect(Collectors.toSet());
  }

  @Override
  protected void moveHologram(final Vector direction) {
    for (final Player viewer : this.getViewers()) {
      for (final IHologramLine<?> line : this.lines) {
        line.sendMove(viewer, direction);
      }
    }
  }
}
