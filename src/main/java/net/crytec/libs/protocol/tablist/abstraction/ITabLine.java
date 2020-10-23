package net.crytec.libs.protocol.tablist.abstraction;

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
public interface ITabLine {

  public void setDisplay(String display);

  public String getDisplay();

  public void setTexture(String texture, String signature);

  public void setTextureBase64(String textureBase64);

  public void send(PlayerConnection connection);

  public void sendDisplayUpdate(PlayerConnection connection);

  public void sendProfileUpdate(PlayerConnection connection);

  public void sendHide(PlayerConnection connection);

}