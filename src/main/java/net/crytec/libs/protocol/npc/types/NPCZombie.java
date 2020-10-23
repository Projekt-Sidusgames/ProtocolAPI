package net.crytec.libs.protocol.npc.types;

import net.crytec.libs.protocol.npc.NPC;
import net.minecraft.server.v1_16_R2.EntityTypes;
import net.minecraft.server.v1_16_R2.EntityZombie;
import net.minecraft.server.v1_16_R2.EnumItemSlot;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NPCZombie extends NPC<EntityZombie> {

  public NPCZombie(final Location location) {
    super(location);
  }

  @Override
  protected EntityTypes<?> getType() {
    return EntityTypes.ZOMBIE;
  }

  public void setBaby(final boolean baby) {
    super.getFakeEntity().setBaby(true);
    super.updateMetadata();
    super.sendPacketNearby(this.getMetaDataPacket());
  }

  public void setItemInHand(final ItemStack item) {
    super.getFakeEntity().setSlot(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(item));
    super.updateMetadata();
    super.sendPacketNearby(this.getMetaDataPacket());
  }

}
