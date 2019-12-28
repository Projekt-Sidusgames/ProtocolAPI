/*
 * ************************************************************************
 *
 * AvarionCraft.de - Created at 08.12.19, 20:22	 by crysis992
 *  __________________
 *
 * [2016] - [2019] AvarionCraft.de
 * All Rights Reserved.
 * net.crytec.util.packets.wrapper.WrapperPlayServerScoreboardDisplayObjective can not be copied and/or distributed without the express
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

public class WrapperPlayServerScoreboardDisplayObjective extends AbstractPacket {

  public static final PacketType TYPE =
      PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE;

  public WrapperPlayServerScoreboardDisplayObjective() {
    super(new PacketContainer(TYPE), TYPE);
    this.handle.getModifier().writeDefaults();
  }

  public WrapperPlayServerScoreboardDisplayObjective(final PacketContainer packet) {
    super(packet, TYPE);
  }

  /**
   * Retrieve Position.
   * <p>
   * Notes: the position of the scoreboard. 0 = list, 1 = sidebar, 2 = belowName.
   *
   * @return The current Position
   */
  public int getPosition() {
    return this.handle.getIntegers().read(0);
  }

  /**
   * Set Position.
   *
   * @param value - new value.
   */
  public void setPosition(final int value) {
    this.handle.getIntegers().write(0, value);
  }

  /**
   * Retrieve Score Name.
   * <p>
   * Notes: the unique name for the scoreboard to be displayed.
   *
   * @return The current Score Name
   */
  public String getScoreName() {
    return this.handle.getStrings().read(0);
  }

  /**
   * Set Score Name.
   *
   * @param value - new value.
   */
  public void setScoreName(final String value) {
    this.handle.getStrings().write(0, value);
  }

}