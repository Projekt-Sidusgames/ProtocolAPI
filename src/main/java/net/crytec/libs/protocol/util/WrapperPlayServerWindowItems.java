package net.crytec.libs.protocol.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class WrapperPlayServerWindowItems extends AbstractPacket {

  public static final PacketType TYPE = PacketType.Play.Server.WINDOW_ITEMS;

  public WrapperPlayServerWindowItems() {
    super(new PacketContainer(TYPE), TYPE);
    this.handle.getModifier().writeDefaults();
  }

  public WrapperPlayServerWindowItems(final PacketContainer packet) {
    super(packet, TYPE);
  }

  /**
   * Retrieve Window ID.
   * <p>
   * Notes: the id of window which items are being sent for. 0 for player inventory.
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
   * Retrieve Slot data.
   *
   * @return The current Slot data
   */
  public List<ItemStack> getSlotData() {
    return this.handle.getItemListModifier().read(0);
  }

  /**
   * Set Slot data.
   *
   * @param value - new value.
   */
  public void setSlotData(final List<ItemStack> value) {
    this.handle.getItemListModifier().write(0, value);
  }

}
