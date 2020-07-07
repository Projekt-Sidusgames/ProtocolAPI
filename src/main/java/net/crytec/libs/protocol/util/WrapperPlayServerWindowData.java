package net.crytec.libs.protocol.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerWindowData extends AbstractPacket {

  public static final PacketType TYPE = PacketType.Play.Server.WINDOW_DATA;

  public WrapperPlayServerWindowData() {
    super(new PacketContainer(TYPE), TYPE);
    this.handle.getModifier().writeDefaults();
  }

  public WrapperPlayServerWindowData(final PacketContainer packet) {
    super(packet, TYPE);
  }

  /**
   * Retrieve Window ID.
   * <p>
   * Notes: the id of the window.
   *
   * @return The current Window ID
   */
  public int getWindowId() {
    return this.handle.getIntegers().read(0);
  }

  /**
   * Set Window ID.
   *
   * @param value - new value.
   */
  public void setWindowId(final int value) {
    this.handle.getIntegers().write(0, value);
  }

  /**
   * Retrieve Property.
   * <p>
   * Notes: which property should be updated.
   *
   * @return The current Property
   */
  public int getProperty() {
    return this.handle.getIntegers().read(1);
  }

  /**
   * Set Property.
   *
   * @param value - new value.
   */
  public void setProperty(final int value) {
    this.handle.getIntegers().write(1, value);
  }

  /**
   * Retrieve Value.
   * <p>
   * Notes: the new value for the property.
   *
   * @return The current Value
   */
  public int getValue() {
    return this.handle.getIntegers().read(2);
  }

  /**
   * Set Value.
   *
   * @param value - new value.
   */
  public void setValue(final int value) {
    this.handle.getIntegers().write(2, value);
  }

}
