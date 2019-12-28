package net.crytec.libs.protocol.holograms;

import java.util.function.Predicate;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IHologramFactory {

  AbstractHologram supplyHologram(Location location, Predicate<Player> viewFilter, AbstractHologramManager manager);

}
