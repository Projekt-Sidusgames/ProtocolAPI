/*
 * ************************************************************************
 *
 * AvarionCraft.de - Created at 08.12.19, 20:22	 by crysis992
 *  __________________
 *
 * [2016] - [2019] AvarionCraft.de
 * All Rights Reserved.
 * net.crytec.util.scoreboard.TeamPacketListener can not be copied and/or distributed without the express
 *  permission of crysis992
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of AvarionCraft.de and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to AvarionCraft.de
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from AvarionCraft.de.
 *
 */

package net.crytec.libs.protocol.scoreboard;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.crytec.libs.protocol.events.PlayerReceiveTeamEvent;
import net.crytec.libs.protocol.util.WrapperPlayServerScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TeamPacketListener extends PacketAdapter {

  private final JavaPlugin plugin;

  public TeamPacketListener(final JavaPlugin plugin, final ScoreboardManager manager) {
    super(plugin, ListenerPriority.HIGHEST, PacketType.Play.Server.SCOREBOARD_TEAM);
    this.plugin = plugin;
  }

  @Override
  public void onPacketSending(final PacketEvent event) {
    if (!event.getPacketType().equals(PacketType.Play.Server.SCOREBOARD_TEAM)) {
      return;
    }
    final PacketContainer packet = event.getPacket();
    final WrapperPlayServerScoreboardTeam wrapper = new WrapperPlayServerScoreboardTeam(packet);

    final PlayerReceiveTeamEvent teamEvent = new PlayerReceiveTeamEvent(event.getPlayer(), wrapper.getPrefix(), wrapper.getSuffix(), wrapper.getColor(), wrapper);

    Bukkit.getScheduler().runTask(this.plugin, () -> Bukkit.getPluginManager().callEvent(teamEvent));

    wrapper.setColor(teamEvent.getColor());
    wrapper.setPrefix(WrappedChatComponent.fromJson(teamEvent.getPrefix().getJson()));
    wrapper.setSuffix(teamEvent.getSuffix());
  }
}