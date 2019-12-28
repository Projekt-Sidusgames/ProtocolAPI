/*
 * ************************************************************************
 *
 * AvarionCraft.de - Created at 08.12.19, 20:22	 by crysis992
 *  __________________
 *
 * [2016] - [2019] AvarionCraft.de
 * All Rights Reserved.
 * net.crytec.util.packets.wrapper.WrapperPlayServerScoreboardScore can not be copied and/or distributed without the express
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
import com.comphenix.protocol.wrappers.EnumWrappers.ScoreboardAction;

public class WrapperPlayServerScoreboardScore extends AbstractPacket {

  public static final PacketType TYPE =
      PacketType.Play.Server.SCOREBOARD_SCORE;

  public WrapperPlayServerScoreboardScore() {
    super(new PacketContainer(TYPE), TYPE);
    this.handle.getModifier().writeDefaults();
  }

  public WrapperPlayServerScoreboardScore(final PacketContainer packet) {
    super(packet, TYPE);
  }

  /**
   * Retrieve Score name.
   * <p>
   * Notes: the name of the score to be updated or removed.
   *
   * @return The current Score name
   */
  public String getScoreName() {
    return this.handle.getStrings().read(0);
  }

  /**
   * Set Score name.
   *
   * @param value - new value.
   */
  public void setScoreName(final String value) {
    this.handle.getStrings().write(0, value);
  }

  /**
   * Retrieve Objective Name.
   * <p>
   * Notes: the name of the objective the score belongs to.
   *
   * @return The current Objective Name
   */
  public String getObjectiveName() {
    return this.handle.getStrings().read(1);
  }

  /**
   * Set Objective Name.
   *
   * @param value - new value.
   */
  public void setObjectiveName(final String value) {
    this.handle.getStrings().write(1, value);
  }

  /**
   * Retrieve Value.
   * <p>
   * Notes: the score to be displayed next to the entry. Only sent when Update/Remove does not equal 1.
   *
   * @return The current Value
   */
  public int getValue() {
    return this.handle.getIntegers().read(0);
  }

  /**
   * Set Value.
   *
   * @param value - new value.
   */
  public void setValue(final int value) {
    this.handle.getIntegers().write(0, value);
  }

  public ScoreboardAction getAction() {
    return this.handle.getScoreboardActions().read(0);
  }

  public void setScoreboardAction(final ScoreboardAction value) {
    this.handle.getScoreboardActions().write(0, value);
  }

}