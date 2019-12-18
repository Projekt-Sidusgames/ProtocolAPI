package net.crytec.libs.protocol.npc.types;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.crytec.libs.protocol.npc.NPC;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.EntityTypes;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class NPCPlayer extends NPC<EntityPlayer> {

  public NPCPlayer(final Location location) {
    super(location);
  }

  @Override
  protected EntityTypes<?> getType() {
    return EntityTypes.PLAYER;
  }

  @Override
  public void onPlayerInteraction(final Player player) {

  }

  public void setSkin(final UUID uuid, final String name) {
    final EntityPlayer entity = super.getFakeEntity();
    entity.setProfile(new GameProfile(uuid, name));
    this.updateMetadata();
    this.sendPacketNearby(this.getMetaDataPacket());
  }

}
