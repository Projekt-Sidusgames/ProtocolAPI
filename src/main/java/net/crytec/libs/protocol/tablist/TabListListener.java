package net.crytec.libs.protocol.tablist;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 10.12.2019
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class TabListListener implements Listener {

  public TabListListener(final TabListManager tablistManager) {
    this.tablistManager = tablistManager;
  }

  private final TabListManager tablistManager;

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    tablistManager.addPlayer(event.getPlayer());
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    tablistManager.removePlayer(event.getPlayer());
  }

}
