package net.crytec.libs.protocol.holograms;

import java.util.UUID;
import java.util.function.Predicate;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IHologramFactory {

  AbstractHologram supplyHologram(Location location, Predicate<Player> viewFilter, AbstractHologramManager manager, UUID uid);

  default AbstractHologram supplyHologram(Location location, Predicate<Player> viewFilter, AbstractHologramManager manager) {
    return supplyHologram(location, viewFilter, manager, UUID.randomUUID());
  }

}
