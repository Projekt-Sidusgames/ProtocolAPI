package net.crytec.libs.protocol.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.inventory.ItemStack;

public class WrapperPlayClientSetCreativeSlot extends AbstractPacket {

  public static final PacketType TYPE =
      PacketType.Play.Client.SET_CREATIVE_SLOT;

  public WrapperPlayClientSetCreativeSlot() {
    super(new PacketContainer(TYPE), TYPE);
    this.handle.getModifier().writeDefaults();
  }

  public WrapperPlayClientSetCreativeSlot(final PacketContainer packet) {
    super(packet, TYPE);
  }

  /**
   * Retrieve Slot.
   * <p>
   * Notes: inventory slot
   *
   * @return The current Slot
   */
  public int getSlot() {
    return this.handle.getIntegers().read(0);
  }

  /**
   * Set Slot.
   *
   * @param value - new value.
   */
  public void setSlot(final int value) {
    this.handle.getIntegers().write(0, value);
  }

  /**
   * Retrieve Clicked item.
   *
   * @return The current Clicked item
   */
  public ItemStack getClickedItem() {
    return this.handle.getItemModifier().read(0);
  }

  /**
   * Set Clicked item.
   *
   * @param value - new value.
   */
  public void setClickedItem(final ItemStack value) {
    this.handle.getItemModifier().write(0, value);
  }

}