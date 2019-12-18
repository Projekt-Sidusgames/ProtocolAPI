package net.crytec.libs.protocol.npc.types;

import net.crytec.libs.protocol.npc.NPC;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.EntityVillager;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftVillager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;

public class NPCVillager extends NPC<EntityVillager> {


  public NPCVillager(final Location location) {
    super(location);
  }

  @Override
  protected EntityTypes<?> getType() {
    return EntityTypes.VILLAGER;
  }

  @Override
  public void onPlayerInteraction(final Player player) {
    player.sendMessage("Yay! Ich bin ein NPC!");
  }

  public void setProfession(final Profession profession) {
    Validate.notNull(profession);
    this.getFakeEntity().setVillagerData(this.getFakeEntity().getVillagerData().withProfession(CraftVillager.bukkitToNmsProfession(profession)));
    this.updateMetadata();
    this.sendPacketNearby(this.getMetaDataPacket());
  }

}
