/*
 * ************************************************************************
 *
 * AvarionCraft.de - Created at 08.12.19, 20:22	 by crysis992
 *  __________________
 *
 * [2016] - [2019] AvarionCraft.de
 * All Rights Reserved.
 * net.crytec.util.packets.wrapper.WrapperPlayServerScoreboardObjective can not be copied and/or distributed without the express
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
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class WrapperPlayServerScoreboardObjective extends AbstractPacket {

  public static final PacketType TYPE =
      PacketType.Play.Server.SCOREBOARD_OBJECTIVE;

  public WrapperPlayServerScoreboardObjective() {
    super(new PacketContainer(TYPE), TYPE);
    this.handle.getModifier().writeDefaults();
  }

  public WrapperPlayServerScoreboardObjective(final PacketContainer packet) {
    super(packet, TYPE);
  }

  /**
   * Enum containing all known packet modes.
   *
   * @author dmulloy2
   */
  public static class Mode extends IntEnum {

    public static final int ADD_OBJECTIVE = 0;
    public static final int REMOVE_OBJECTIVE = 1;
    public static final int UPDATE_VALUE = 2;

    private static final Mode INSTANCE = new Mode();

    public static Mode getInstance() {
      return INSTANCE;
    }
  }

  /**
   * Retrieve Objective name.
   * <p>
   * Notes: an unique name for the objective
   *
   * @return The current Objective name
   */
  public String getName() {
    return this.handle.getStrings().read(0);
  }

  /**
   * Set Objective name.
   *
   * @param value - new value.
   */
  public void setName(final String value) {
    this.handle.getStrings().write(0, value);
  }

  /**
   * Retrieve Objective DisplayName.
   * <p>
   * Notes: only if mode is 0 or 2. The text to be displayed for the score.
   *
   * @return The current Objective value
   */
  public WrappedChatComponent getDisplayName() {
    return this.handle.getChatComponents().read(0);
  }

  /**
   * Set Objective DisplayName.
   *
   * @param value - new value.
   */
  public void setDisplayName(final WrappedChatComponent value) {
    this.handle.getChatComponents().write(0, value);
  }

  /**
   * Retrieve health display.
   * <p>
   * Notes: Can be either INTEGER or HEARTS
   *
   * @return the current health display value
   */
  public HealthDisplay getHealthDisplay() {
    return this.handle.getEnumModifier(HealthDisplay.class, 2).read(0);
  }

  /**
   * Set health display.
   *
   * @param value - value
   * @see #getHealthDisplay()
   */
  public void setHealthDisplay(final HealthDisplay value) {
    this.handle.getEnumModifier(HealthDisplay.class, 2).write(0, value);
  }

  /**
   * Retrieve Mode.
   * <p>
   * Notes: 0 to create the scoreboard. 1 to remove the scoreboard. 2 to update the display text.
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

  public enum HealthDisplay {
    INTEGER, HEARTS
  }
}