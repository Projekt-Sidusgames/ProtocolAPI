package net.crytec.libs.protocol.npc.types;

import net.crytec.libs.protocol.npc.NPC;
import net.minecraft.server.v1_16_R2.EntityTypes;
import net.minecraft.server.v1_16_R2.EntityVillager;
import net.minecraft.server.v1_16_R2.IRegistry;
import net.minecraft.server.v1_16_R2.VillagerData;
import net.minecraft.server.v1_16_R2.VillagerType;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_16_R2.util.CraftNamespacedKey;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;

public class NPCVillager extends NPC<EntityVillager> {


  public NPCVillager(final Location location) {
    super(location);
  }

  @Override
  protected EntityTypes<?> getType() {
    return EntityTypes.VILLAGER;
  }

  public void setProfession(final Profession profession) {
    Validate.notNull(profession);
    final EntityVillager nmsVillager = this.getFakeEntity();
    final VillagerData villagerData = nmsVillager.getVillagerData().withProfession(CraftVillager.bukkitToNmsProfession(profession));
    this.getFakeEntity().setVillagerData(villagerData);
    this.updateMetadata();
    this.sendPacketNearby(this.getMetaDataPacket());
    this.getLocation().getWorld().playSound(this.getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 1F, 1F);
  }

  public void setType(final Villager.Type type) {
    Validate.notNull(type);
    final EntityVillager nmsVillager = this.getFakeEntity();
    final VillagerType nmsType = IRegistry.VILLAGER_TYPE.get(CraftNamespacedKey.toMinecraft(type.getKey()));
    final VillagerData villagerData = nmsVillager.getVillagerData().withType(nmsType);
    this.getFakeEntity().setVillagerData(villagerData);
    this.updateMetadata();
    this.sendPacketNearby(this.getMetaDataPacket());
    this.getLocation().getWorld().playSound(this.getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 1F, 1F);
  }

}
