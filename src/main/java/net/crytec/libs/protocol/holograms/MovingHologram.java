package net.crytec.libs.protocol.holograms;

import org.bukkit.util.Vector;

public class MovingHologram {

  public MovingHologram(AbstractHologram hologram, Vector direction, int ticks) {
    this.hologram = hologram;
    this.direction = direction;
    this.ticks = ticks;
  }

  private final AbstractHologram hologram;
  private final Vector direction;
  private final int ticks;
  private int ticksLived = 0;

  public AbstractHologram getHologram() {
    return this.hologram;
  }

  public boolean isAlive() {
    return this.ticks > this.ticksLived;
  }

  protected void onTick() {
    ticksLived++;
    this.hologram.move(this.direction);
  }

}