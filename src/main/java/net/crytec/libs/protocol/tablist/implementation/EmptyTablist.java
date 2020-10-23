package net.crytec.libs.protocol.tablist.implementation;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Set;
import net.crytec.libs.protocol.tablist.TabListManager;
import net.crytec.libs.protocol.tablist.abstraction.ITabLine;
import net.crytec.libs.protocol.tablist.abstraction.ITabList;
import net.minecraft.server.v1_16_R2.ChatMessage;
import net.minecraft.server.v1_16_R2.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_16_R2.PlayerConnection;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 10.12.2019
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class EmptyTablist implements ITabList {

  public EmptyTablist(final TabListManager tabListManager) {
    this.playerConnectionSet = Sets.newHashSet();
    this.tabs = Lists.newArrayList();
    this.tabListManager = tabListManager;
    this.headerFooterPacket = new PacketPlayOutPlayerListHeaderFooter();
    this.headerFooterPacket.header = new ChatMessage("EMPTY_HEADER");
    this.headerFooterPacket.footer = new ChatMessage("EMPTY_FOOTER");
  }

  private final TabListManager tabListManager;
  private final Set<PlayerConnection> playerConnectionSet;
  protected final ArrayList<ITabLine> tabs;
  private final PacketPlayOutPlayerListHeaderFooter headerFooterPacket;

  @Override
  public void addViewer(final PlayerConnection connection) {
    this.playerConnectionSet.add(connection);
  }

  @Override
  public void removeViewer(final PlayerConnection connection) {
    this.playerConnectionSet.remove(connection);
  }

  @Override
  public Set<PlayerConnection> getViewers() {
    return this.playerConnectionSet;
  }

  @Override
  public int getSize() {
    return this.tabs.size();
  }

  @Override
  public ITabLine getLine(final int index) {
    return this.tabs.get(index);
  }

  @Override
  public void setHeader(final String header) {
    this.headerFooterPacket.header = new ChatMessage(header);
  }

  @Override
  public String getHeader() {
    return this.headerFooterPacket.header.getText();
  }

  @Override
  public void setFooter(final String footer) {
    this.headerFooterPacket.footer = new ChatMessage(footer);
  }

  @Override
  public String getFooter() {
    return this.headerFooterPacket.footer.getText();
  }

  @Override
  public void sendHeaderFooter(final PlayerConnection connection) {
    connection.sendPacket(this.headerFooterPacket);
  }

  @Override
  public void addLine(final ITabLine line) {
    this.tabs.add(line);
  }

}