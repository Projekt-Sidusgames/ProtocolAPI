package net.crytec.libs.protocol.npc.types;

import net.crytec.libs.protocol.npc.NPC;
import net.minecraft.server.v1_16_R1.EntityTypes;
import net.minecraft.server.v1_16_R1.EntityWitch;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class NPCWitch extends NPC<EntityWitch> {

  public NPCWitch(final Location location) {
    super(location);
  }

  @Override
  protected EntityTypes<?> getType() {
    return EntityTypes.WITCH;
  }

  public final void setDrinkingPotion(final ItemStack potion) {
    final EntityWitch witch = this.getFakeEntity();
    witch.setDrinkingPotion(net.minecraft.server.v1_16_R1.ItemStack.fromBukkitCopy(potion));
    this.updateMetadata();
    this.sendPacketNearby(this.getMetaDataPacket());
  }
}