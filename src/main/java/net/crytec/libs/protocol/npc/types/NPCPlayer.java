package net.crytec.libs.protocol.npc.types;

import net.crytec.libs.protocol.npc.NPC;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EntityTypes;
import org.bukkit.Location;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of protocol and was created at the 25.01.2021
 *
 * protocol can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class NPCPlayer extends NPC<EntityPlayer> {

  public NPCPlayer(Location location) {
    super(location);
  }

  @Override
  protected EntityTypes<?> getType() {
    return EntityTypes.PLAYER;
  }
}
