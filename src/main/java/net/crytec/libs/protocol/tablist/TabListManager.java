package net.crytec.libs.protocol.tablist;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Function;
import net.crytec.libs.protocol.tablist.abstraction.ITabList;
import net.crytec.libs.protocol.tablist.abstraction.TabView;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 10.12.2019
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class TabListManager {

  public TabListManager(final JavaPlugin plugin, final Function<Player, ITabList> defaultTablistProvider) {
    this.tabViewMap = Maps.newHashMap();
    Bukkit.getPluginManager().registerEvents(new TabListListener(this), plugin);
    this.defaultTablistProvider = defaultTablistProvider;
  }

  private final Map<Player, TabView> tabViewMap;
  private final Function<Player, ITabList> defaultTablistProvider;

  public TabView getView(final Player player) {
    return this.tabViewMap.get(player);
  }

  protected void addPlayer(final Player player) {
    this.tabViewMap.put(player, new TabView(player));
    this.tabViewMap.get(player).setTablist(this.defaultTablistProvider.apply(player));
    this.tabViewMap.get(player).setAndUpdate(this.defaultTablistProvider.apply(player));
  }

  protected void removePlayer(final Player player) {
    this.tabViewMap.remove(player);
  }

}