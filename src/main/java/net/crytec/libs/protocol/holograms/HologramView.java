package net.crytec.libs.protocol.holograms;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.entity.Player;

public class HologramView implements Iterable<AbstractHologram>{

  public HologramView(Player player) {
    this.player = player;
    this.viewingHolograms = Sets.newHashSet();
  }

  private final Player player;
  private final Set<AbstractHologram> viewingHolograms;

  public boolean isViewing(AbstractHologram hologram) {
    return this.viewingHolograms.contains(hologram);
  }

  public void addHologram(AbstractHologram hologram) {
    this.viewingHolograms.add(hologram);
    hologram.showTo(player);
  }

  public void removeHologram(AbstractHologram hologram) {
    this.viewingHolograms.remove(hologram);
    hologram.hideFrom(player);
  }

  @Override
  public Iterator<AbstractHologram> iterator() {
    return this.viewingHolograms.iterator();
  }

}