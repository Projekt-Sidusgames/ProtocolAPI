package net.crytec.libs.protocol.npc.types;

import net.crytec.libs.protocol.npc.NPC;
import net.minecraft.server.v1_15_R1.EntityIronGolem;
import net.minecraft.server.v1_15_R1.EntityTypes;
import org.bukkit.Location;

public class NPCIronGolem extends NPC<EntityIronGolem> {

  public NPCIronGolem(final Location location) {
    super(location);
  }

  @Override
  protected EntityTypes<?> getType() {
    return EntityTypes.IRON_GOLEM;
  }
}