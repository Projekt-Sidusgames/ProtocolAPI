package net.crytec.libs.protocol.tablist.abstraction;


import lombok.Getter;
import net.minecraft.server.v1_16_R2.PlayerConnection;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 10.12.2019
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class TabView {

  public TabView(final Player player) {
    this.player = player;
    this.connection = ((CraftPlayer) player).getHandle().playerConnection;
  }

  @Getter
  private final Player player;
  @Getter
  private final PlayerConnection connection;
  @Getter
  private ITabList tablist;

  public void setTablist(ITabList newTablist) {
    if (this.tablist != null) {
      this.tablist.removeViewer(connection);
    }
    this.tablist = newTablist;
    newTablist.addViewer(connection);
  }

  public void setAndUpdate(final ITabList newTablist) {
    this.tablist.hideFrom(connection);
    setTablist(newTablist);
    this.tablist.showTo(connection);
  }

}