package net.crytec.libs.protocol.holograms.impl;

import java.util.function.Predicate;
import net.crytec.libs.protocol.holograms.AbstractHologram;
import net.crytec.libs.protocol.holograms.AbstractHologramManager;
import net.crytec.libs.protocol.holograms.IHologramFactory;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HologramFactory implements IHologramFactory {

  @Override
  public AbstractHologram supplyHologram(final Location location, final Predicate<Player> viewFilter,
      final AbstractHologramManager manager) {
    return new Hologram(location, viewFilter, manager);
  }
}
