package net.crytec.libs.protocol.npc.types;

import net.crytec.libs.protocol.npc.NPC;
import net.minecraft.server.v1_15_R1.EntitySkeleton;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.EnumItemSlot;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
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
    super.getFakeEntity().setEquipment(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(item));
    super.updateMetadata();
    super.sendPacketNearby(this.getMetaDataPacket());
  }
}