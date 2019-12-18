package net.crytec.libs.protocol.tablist.implementation;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import lombok.Getter;
import net.crytec.libs.protocol.tablist.abstraction.ITabLine;
import net.minecraft.server.v1_15_R1.ChatMessage;
import net.minecraft.server.v1_15_R1.DimensionManager;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.MinecraftServer;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_15_R1.PlayerConnection;
import net.minecraft.server.v1_15_R1.PlayerInteractManager;
import net.minecraft.server.v1_15_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 10.12.2019
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class TabLine implements ITabLine {

  public TabLine(final int index) {
    this(UUID.randomUUID(), index, "");
  }

  public TabLine(final int index, String display) {
    this(UUID.randomUUID(), index, display);
  }

  public TabLine(final UUID playerID, final int index, final String display) {
    final GameProfile profile = new GameProfile(playerID, " " + (char) index);
    final MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
    final WorldServer worldserver = server.getWorldServer(DimensionManager.OVERWORLD);
    final PlayerInteractManager playerinteractmanager = new PlayerInteractManager(worldserver);
    final EntityPlayer player = new EntityPlayer(server, worldserver, profile, playerinteractmanager);
    player.listName = new ChatMessage(display);
    this.entity = player;
    showPacket = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, player);
    hidePacket = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, player);
    namePacket = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, player);
  }

  @Getter
  private final EntityPlayer entity;
  private PacketPlayOutPlayerInfo showPacket;
  private final PacketPlayOutPlayerInfo hidePacket;
  private final PacketPlayOutPlayerInfo namePacket;

  @Override
  public void setDisplay(final String display) {
    this.entity.listName = new ChatMessage(display);
  }

  @Override
  public String getDisplay() {
    return this.entity.listName.getText();
  }

  @Override
  public void setTexture(String texture, String signature) {
    GameProfile profile = entity.getProfile();
    profile.getProperties().put("textures", new Property("textures", texture, signature));
    showPacket = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, entity);
  }

  @Override
  public void setTextureBase64(String textureBase64) {
    GameProfile profile = entity.getProfile();
    profile.getProperties().removeAll("textures");
    profile.getProperties().put("textures", new Property("textures", textureBase64));
    showPacket = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, entity);
  }

  @Override
  public void send(final PlayerConnection connection) {
    connection.sendPacket(showPacket);
  }

  @Override
  public void sendDisplayUpdate(final PlayerConnection connection) {
    connection.sendPacket(namePacket);
  }

  @Override
  public void sendProfileUpdate(final PlayerConnection connection) {

  }

  @Override
  public void sendHide(final PlayerConnection connection) {
    connection.sendPacket(hidePacket);
  }

}