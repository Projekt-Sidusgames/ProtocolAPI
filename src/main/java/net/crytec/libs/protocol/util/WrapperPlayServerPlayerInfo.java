/**
 * PacketWrapper - ProtocolLib wrappers for Minecraft packets Copyright (C) dmulloy2 <http://dmulloy2.net> Copyright (C) Kristian S. Strangeland
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.crytec.libs.protocol.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import java.util.List;

public class WrapperPlayServerPlayerInfo extends AbstractPacket {

  public static final PacketType TYPE = PacketType.Play.Server.PLAYER_INFO;

  public WrapperPlayServerPlayerInfo() {
    super(new PacketContainer(TYPE), TYPE);
    this.handle.getModifier().writeDefaults();
  }

  public WrapperPlayServerPlayerInfo(final PacketContainer packet) {
    super(packet, TYPE);
  }

  public PlayerInfoAction getAction() {
    return this.handle.getPlayerInfoAction().read(0);
  }

  public void setAction(final PlayerInfoAction value) {
    this.handle.getPlayerInfoAction().write(0, value);
  }

  public List<PlayerInfoData> getData() {
    return this.handle.getPlayerInfoDataLists().read(0);
  }

  public void setData(final List<PlayerInfoData> value) {
    this.handle.getPlayerInfoDataLists().write(0, value);
  }
}