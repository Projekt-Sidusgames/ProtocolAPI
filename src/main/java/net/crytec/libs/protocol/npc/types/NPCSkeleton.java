package net.crytec.libs.protocol.npc.types;

import net.crytec.libs.protocol.npc.NPC;
import net.minecraft.server.v1_16_R3.EntitySkeleton;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NPCSkeleton extends NPC<EntitySkeleton> {

  public NPCSkeleton(final Location location) {
    super(location);
  }

  @Override
  protected EntityTypes<?> getType() {
    return EntityTypes.SKELETON;
  }

  public void setItemInHand(final ItemStack item) {
    super.getFakeEntity().setSlot(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(item));
    super.updateMetadata();
    super.sendPacketNearby(this.getMetaDataPacket());
  }
}