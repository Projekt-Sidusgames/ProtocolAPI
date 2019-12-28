/*
 * ************************************************************************
 *
 * AvarionCraft.de - Created at 08.12.19, 20:22	 by crysis992
 *  __________________
 *
 * [2016] - [2019] AvarionCraft.de
 * All Rights Reserved.
 * net.crytec.util.scoreboard.PlayerBoard can not be copied and/or distributed without the express
 *  permission of crysis992
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of AvarionCraft.de and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to AvarionCraft.de
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from AvarionCraft.de.
 *
 */

package net.crytec.libs.protocol.scoreboard;

import com.comphenix.protocol.wrappers.EnumWrappers.ScoreboardAction;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.crytec.libs.protocol.scoreboard.api.Entry;
import net.crytec.libs.protocol.scoreboard.api.ScoreboardHandler;
import net.crytec.libs.protocol.util.WrapperPlayServerScoreboardDisplayObjective;
import net.crytec.libs.protocol.util.WrapperPlayServerScoreboardObjective;
import net.crytec.libs.protocol.util.WrapperPlayServerScoreboardObjective.HealthDisplay;
import net.crytec.libs.protocol.util.WrapperPlayServerScoreboardScore;
import net.crytec.libs.protocol.util.WrapperPlayServerScoreboardTeam;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.IntSet;
import org.bukkit.entity.Player;

public class PlayerBoard {

  private static final List<String> teams = IntStream.rangeClosed(0, 15).mapToObj(i -> ChatColor.values()[i].toString()).collect(Collectors.toList());
  private final Player player;

  private final Int2ObjectOpenHashMap<String> cache;
  private final IntSet cache2 = new IntOpenHashSet();

  private boolean activated;
  private ScoreboardHandler handler;
  private boolean hidden = false;

  public PlayerBoard(final Player player) {
    this.player = player;
    this.cache = new Int2ObjectOpenHashMap<>(15);
  }


  public void activate() {
    if (this.activated) {
      return;
    }
    if (this.handler == null) {
      throw new IllegalStateException("Scoreboard handler not set");
    }

    this.activated = true;
    this.cache.clear();

    // Register Teams
    for (int i = 0; i < 15; i++) {
      final WrapperPlayServerScoreboardTeam line = new WrapperPlayServerScoreboardTeam();
      line.setName(teams.get(i));
      line.setPlayers(Collections.singletonList(teams.get(i)));
      line.setMode(0);
      line.sendPacket(this.player);
    }

    if (this.hidden) {
      final WrapperPlayServerScoreboardObjective display = new WrapperPlayServerScoreboardObjective();
      display.setName("sidebar");
      display.setDisplayName(WrappedChatComponent.fromText(this.handler.getTitle(this.player)));
      display.setMode(0);
      display.sendPacket(this.player);

      final WrapperPlayServerScoreboardDisplayObjective obj = new WrapperPlayServerScoreboardDisplayObjective();
      obj.setPosition(1);
      obj.setScoreName("sidebar");
      obj.sendPacket(this.player);
    }
  }


  public void deactivate() {
    if (!this.activated) {
      return;
    }

    this.activated = false;
    if (!this.player.isOnline()) {
      return;
    }

    for (int i = 0; i < 15; i++) {
      final WrapperPlayServerScoreboardTeam line = new WrapperPlayServerScoreboardTeam();
      line.setName(teams.get(i));
      line.setPlayers(Collections.singletonList(teams.get(i)));
      line.setMode(1);
      line.sendPacket(this.player);
    }

    final WrapperPlayServerScoreboardObjective display = new WrapperPlayServerScoreboardObjective();
    display.setName("sidebar");
    display.setMode(1);
    display.sendPacket(this.player);
    this.hidden = true;

  }


  public void updateLine(final int line, final String text) {
    if (!this.cache.containsKey(line)) {
      this.showLine(line);
    }
    final WrapperPlayServerScoreboardTeam update = new WrapperPlayServerScoreboardTeam();
    update.setName(teams.get(line));
    update.setMode(2);
    update.setSuffix(WrappedChatComponent.fromText(text));
    update.sendPacket(this.player);
    this.cache.put(line, text);
  }

  protected void showLine(final int line) {
    final WrapperPlayServerScoreboardScore score = new WrapperPlayServerScoreboardScore();
    score.setObjectiveName("sidebar");
    score.setValue(line);
    score.setScoreboardAction(ScoreboardAction.CHANGE);
    score.setScoreName(teams.get(line));
    score.sendPacket(this.player);
  }

  protected void hideLine(final int line) {
    final WrapperPlayServerScoreboardScore score = new WrapperPlayServerScoreboardScore();
    score.setObjectiveName("sidebar");
    score.setValue(line);
    score.setScoreboardAction(ScoreboardAction.REMOVE);
    score.setScoreName(teams.get(line));
    score.sendPacket(this.player);
  }


  public void setTitle(final String title) {
    final WrapperPlayServerScoreboardObjective obj = new WrapperPlayServerScoreboardObjective();
    obj.setName("sidebar");
    obj.setMode(2);
    obj.setHealthDisplay(HealthDisplay.INTEGER);
    obj.setDisplayName(WrappedChatComponent.fromText(title));
    obj.sendPacket(this.player);
  }

  public void update() {

    if (!this.player.isOnline()) {
      this.deactivate();
      return;
    }

    this.setTitle(this.handler.getTitle(this.player));

    for (final Entry entry : this.handler.getEntries(this.player)) {
      final String key = entry.getName();
      final int score = entry.getPosition();

      if (key == null) {
        continue;
      }

      this.cache2.add(score);
      if (this.cache.containsKey(score) && this.cache.get(score).equals(key)) {
        continue;
      }
      this.updateLine(score, key);
    }

    for (final int line : this.cache.keySet()) {
      if (this.cache2.contains(line)) {
        continue;
      }

      this.hideLine(line);
      this.cache.remove(line);
    }

    this.cache2.clear();
  }

  public void setHandler(final ScoreboardHandler handler) {
    this.handler = handler;
  }

  public boolean isActivated() {
    return this.activated;
  }

  public Player getHolder() {
    return this.player;
  }
}
