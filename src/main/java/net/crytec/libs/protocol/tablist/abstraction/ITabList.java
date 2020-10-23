package net.crytec.libs.protocol.tablist.abstraction;

import java.util.Set;
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
public interface ITabList {

  public default void updateDisplay(final int index, final String display) {
    final ITabLine tabline = this.getLine(index);
    tabline.setDisplay(display);
    for (final PlayerConnection connection : this.getViewers()) {
      tabline.sendDisplayUpdate(connection);
    }
  }

  public default void updateTexture(final int index, String texture, String signature) {
    final ITabLine tabline = this.getLine(index);
    tabline.setTexture(texture, signature);
    for (final PlayerConnection connection : this.getViewers()) {
      tabline.sendProfileUpdate(connection);
    }
  }

  public abstract void addViewer(PlayerConnection connection);

  public abstract void removeViewer(PlayerConnection connection);

  public abstract Set<PlayerConnection> getViewers();

  public abstract int getSize();

  public abstract ITabLine getLine(int index);

  public abstract void setHeader(String header);

  public abstract String getHeader();

  public abstract void setFooter(String footer);

  public abstract String getFooter();

  public void sendHeaderFooter(PlayerConnection connection);

  public void addLine(ITabLine line);

  public default void broadcastHeaderFooter() {
    for (final PlayerConnection connection : this.getViewers()) {
      this.sendHeaderFooter(connection);
    }
  }

  public default void updateAndSendHeaderFooter(final String header, final String footer) {
    this.setHeader(header);
    this.setFooter(footer);
    this.broadcastHeaderFooter();
  }

  public default void showTo(final PlayerConnection connection) {
    for (int index = 0; index < this.getSize(); index++) {
      this.getLine(index).send(connection);
    }
    this.sendHeaderFooter(connection);
  }

  public default void hideFrom(final PlayerConnection connection) {
    for (int index = 0; index < this.getSize(); index++) {
      this.getLine(index).sendHide(connection);
    }
  }

}