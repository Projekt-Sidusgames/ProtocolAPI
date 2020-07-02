package net.crytec.libs.protocol.holograms.impl;

import java.util.UUID;
import java.util.function.Predicate;
import net.crytec.libs.protocol.holograms.AbstractHologramManager;
import net.crytec.libs.protocol.holograms.IHologramFactory;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HologramFactory implements IHologramFactory {

  @Override
  public Hologram supplyHologram(Location location, Predicate<Player> viewFilter, AbstractHologramManager manager, UUID uid) {
    return new Hologram(location, viewFilter, manager, uid);
  }

}
