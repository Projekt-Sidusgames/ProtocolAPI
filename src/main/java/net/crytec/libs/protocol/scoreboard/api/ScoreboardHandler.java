/*
 * ************************************************************************
 *
 * AvarionCraft.de - Created at 08.12.19, 20:22	 by crysis992
 *  __________________
 *
 * [2016] - [2019] AvarionCraft.de
 * All Rights Reserved.
 * net.crytec.util.scoreboard.api.ScoreboardHandler can not be copied and/or distributed without the express
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

package net.crytec.libs.protocol.scoreboard.api;

import java.util.List;
import org.bukkit.entity.Player;

/**
 * Represents the handler to determine the title and entries of a scoreboard.
 */
public interface ScoreboardHandler {

  /**
   * Determines the title to display for this player. If null returned, title automatically becomes a blank line.
   *
   * @param player player
   * @return title
   */
  public String getTitle(Player player);

  /**
   * Determines the entries to display for this player. If null returned, the entries are not updated.
   *
   * @param player player
   * @return entries
   */
  public List<Entry> getEntries(Player player);

}