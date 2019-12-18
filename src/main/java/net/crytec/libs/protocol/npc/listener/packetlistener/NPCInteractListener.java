package net.crytec.libs.protocol.npc.listener.packetlistener;

import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import com.comphenix.protocol.wrappers.EnumWrappers.Hand;
import java.util.Optional;
import net.crytec.libs.protocol.npc.NPC;
import net.crytec.libs.protocol.npc.manager.NpcManager;
import net.crytec.libs.protocol.util.WrapperPlayClientUseEntity;
import org.bukkit.plugin.Plugin;

public class NPCInteractListener extends PacketAdapter {

  private final NpcManager manager;

  public NPCInteractListener(final Plugin plugin, final NpcManager manager) {
    super(plugin, Client.USE_ENTITY);
    this.manager = manager;
    plugin.getLogger().info("Setup NPC packet adapter!");
  }

  @Override
  public void onPacketReceiving(final PacketEvent event) {
    final WrapperPlayClientUseEntity wrapper = new WrapperPlayClientUseEntity(event.getPacket());
    final int targetID = wrapper.getTargetID();

    if (wrapper.getType() != EntityUseAction.INTERACT || !this.manager.isNPC(targetID)) {
      return;
    }

    final Optional<Hand> optionalHand = event.getPacket().getHands().optionRead(0);

    optionalHand.filter(hand -> hand.equals(Hand.MAIN_HAND)).ifPresent(hand -> {
      final NPC<?> npc = this.manager.getNPC(targetID);
      npc.onPlayerInteraction(event.getPlayer());
    });
  }
}
