/*
 * ************************************************************************
 *
 * AvarionCraft.de - Created at 08.12.19, 20:22	 by crysis992
 *  __________________
 *
 * [2016] - [2019] AvarionCraft.de
 * All Rights Reserved.
 * net.crytec.util.packets.wrapper.WrapperPlayServerScoreboardTeam can not be copied and/or distributed without the express
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

package net.crytec.libs.protocol.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.IntEnum;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import java.util.Collection;
import java.util.List;
import org.bukkit.ChatColor;

public class WrapperPlayServerScoreboardTeam extends AbstractPacket {

  public static final PacketType TYPE =
      PacketType.Play.Server.SCOREBOARD_TEAM;

  public WrapperPlayServerScoreboardTeam() {
    super(new PacketContainer(TYPE), TYPE);
    this.handle.getModifier().writeDefaults();
  }

  public WrapperPlayServerScoreboardTeam(final PacketContainer packet) {
    super(packet, TYPE);
  }

  /**
   * Enum containing all known modes.
   *
   * @author dmulloy2
   */
  public static class Mode extends IntEnum {

    public static final int TEAM_CREATED = 0;
    public static final int TEAM_REMOVED = 1;
    public static final int TEAM_UPDATED = 2;
    public static final int PLAYERS_ADDED = 3;
    public static final int PLAYERS_REMOVED = 4;

    private static final Mode INSTANCE = new Mode();

    public static Mode getInstance() {
      return INSTANCE;
    }
  }

  /**
   * Retrieve Team Name.
   * <p>
   * Notes: a unique name for the team. (Shared with scoreboard).
   *
   * @return The current Team Name
   */
  public String getName() {
    return this.handle.getStrings().read(0);
  }

  /**
   * Set Team Name.
   *
   * @param value - new value.
   */
  public void setName(final String value) {
    this.handle.getStrings().write(0, value);
  }

  /**
   * Retrieve Team Display Name.
   * <p>
   * Notes: only if Mode = 0 or 2.
   *
   * @return The current Team Display Name
   */
  public WrappedChatComponent getDisplayName() {
    return this.handle.getChatComponents().read(0);
  }

  /**
   * Set Team Display Name.
   *
   * @param value - new value.
   */
  public void setDisplayName(final WrappedChatComponent value) {
    this.handle.getChatComponents().write(0, value);
  }

  /**
   * Retrieve Team Prefix.
   * <p>
   * Notes: only if Mode = 0 or 2. Displayed before the players' name that are part of this team.
   *
   * @return The current Team Prefix
   */
  public WrappedChatComponent getPrefix() {
    return this.handle.getChatComponents().read(1);
  }

  /**
   * Set Team Prefix.
   *
   * @param value - new value.
   */
  public void setPrefix(final WrappedChatComponent value) {
    this.handle.getChatComponents().write(1, value);
  }

  /**
   * Retrieve Team Suffix.
   * <p>
   * Notes: only if Mode = 0 or 2. Displayed after the players' name that are part of this team.
   *
   * @return The current Team Suffix
   */
  public WrappedChatComponent getSuffix() {
    return this.handle.getChatComponents().read(2);
  }

  /**
   * Set Team Suffix.
   *
   * @param value - new value.
   */
  public void setSuffix(final WrappedChatComponent value) {
    this.handle.getChatComponents().write(2, value);
  }

  /**
   * Retrieve Name Tag Visibility.
   * <p>
   * Notes: only if Mode = 0 or 2. always, hideForOtherTeams, hideForOwnTeam, never.
   *
   * @return The current Name Tag Visibility
   */
  public String getNameTagVisibility() {
    return this.handle.getStrings().read(1);
  }

  /**
   * Set Name Tag Visibility.
   *
   * @param value - new value.
   */
  public void setNameTagVisibility(final String value) {
    this.handle.getStrings().write(1, value);
  }

  /**
   * Retrieve Color.
   * <p>
   * Notes: only if Mode = 0 or 2. Same as Chat colors.
   *
   * @return The current Color
   */
  public ChatColor getColor() {
    return this.handle.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).read(0);
  }

  /**
   * Set Color.
   *
   * @param value - new value.
   */
  public void setColor(final ChatColor value) {
    this.handle.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0, value);
  }

  /**
   * Get the collision rule. Notes: only if Mode = 0 or 2. always, pushOtherTeams, pushOwnTeam, never.
   *
   * @return The current collision rule
   */
  public String getCollisionRule() {
    return this.handle.getStrings().read(2);
  }

  /**
   * Sets the collision rule.
   *
   * @param value - new value.
   */
  public void setCollisionRule(final String value) {
    this.handle.getStrings().write(2, value);
  }

  /**
   * Retrieve Players.
   * <p>
   * Notes: only if Mode = 0 or 3 or 4. Players to be added/remove from the team. Max 40 characters so may be uuid's later
   *
   * @return The current Players
   */
  public List<String> getPlayers() {
    return (List<String>) this.handle.getSpecificModifier(Collection.class)
        .read(0);
  }

  /**
   * Set Players.
   *
   * @param value - new value.
   */
  public void setPlayers(final List<String> value) {
    this.handle.getSpecificModifier(Collection.class).write(0, value);
  }

  /**
   * Retrieve Mode.
   * <p>
   * Notes: if 0 then the team is created. If 1 then the team is removed. If 2 the team team information is updated. If 3 then new players are added to the team. If 4 then players are removed from the team.
   *
   * @return The current Mode
   */
  public int getMode() {
    return this.handle.getIntegers().read(0);
  }

  /**
   * Set Mode.
   *
   * @param value - new value.
   */
  public void setMode(final int value) {
    this.handle.getIntegers().write(0, value);
  }

  /**
   * Retrieve pack option data. Pack data is calculated as follows:
   *
   * <pre>
   * <code>
   * int data = 0;
   * if (team.allowFriendlyFire()) {
   *     data |= 1;
   * }
   * if (team.canSeeFriendlyInvisibles()) {
   *     data |= 2;
   * }
   * </code>
   * </pre>
   *
   * @return The current pack option data
   */
  public int getPackOptionData() {
    return this.handle.getIntegers().read(1);
  }

  /**
   * Set pack option data.
   *
   * @param value - new value
   * @see #getPackOptionData()
   */
  public void setPackOptionData(final int value) {
    this.handle.getIntegers().write(1, value);
  }
}