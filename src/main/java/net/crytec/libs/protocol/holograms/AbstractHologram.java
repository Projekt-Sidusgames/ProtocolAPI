package net.crytec.libs.protocol.holograms;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public abstract class AbstractHologram {

  private static final double MAX_MOVE_DIST = 8 * 8;

  public AbstractHologram(Location baseLocation, Predicate<Player> playerFilter, AbstractHologramManager manager) {
    this(baseLocation, playerFilter, manager, UUID.randomUUID());
  }

  public AbstractHologram(Location baseLocation, Predicate<Player> playerFilter, AbstractHologramManager manager, UUID uuid) {
    this.lines = Lists.newArrayList();
    this.manager = manager;
    this.baseLocation = baseLocation;
    this.playerFilter = playerFilter;
    this.clickable = false;
    this.holoID = uuid;
  }

  protected final AbstractHologramManager manager;
  private final Location baseLocation;
  protected final ArrayList<IHologramLine<?>> lines;
  private Predicate<Player> playerFilter;
  protected boolean clickable;
  @Getter
  private final UUID holoID;

  protected void registerClickableEntities() {
    this.manager.setClickableIdentifier(this.getClickableEntityIds(), this);
  }

  public void delete() {
    this.manager.removeHologram(this);
  }

  public Set<Player> getViewers() {
    return this.manager.getViewing(this);
  }

  public void move(Vector direction) {
    Preconditions.checkArgument(direction.lengthSquared() < MAX_MOVE_DIST, "Move distance can be 8 at most.");
    this.moveHologram(direction);
  }

  public int getSize() {
    return this.lines.size();
  }

  public void setPlayerFilter(Predicate<Player> filter) {
    this.playerFilter = filter;
  }

  public boolean isViableViewer(Player player) {
    return this.playerFilter.test(player);
  }

  protected void appendLine(IHologramLine<?> line) {
    this.lines.add(line);
    for(Player viewer : this.getViewers()) {
      line.showTo(viewer);
    }
  }

  public IHologramLine<?> getHologramLine(int index){
    return lines.get(index);
  }

  public void showTo(Player player) {
    for(IHologramLine<?> line : lines) {
      line.showTo(player);
      this.showClickableEntities(player);
    }
  }

  public void hideFrom(Player player) {
    for(IHologramLine<?> line : lines) {
      line.hideFrom(player);
      this.hideClickableEntities(player);
    }
  }

  public Location getBaseLocation() {
    return this.baseLocation;
  }

  public abstract void setClickable();
  protected abstract void showClickableEntities(Player player);
  protected abstract void hideClickableEntities(Player player);
  protected abstract Set<Integer> getClickableEntityIds();
  protected abstract void moveHologram(Vector direction);
  public abstract void appendTextLine(String text);
  public abstract void appendItemLine(ItemStack item);

}