package net.crytec.libs.protocol.skinclient;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import net.crytec.libs.protocol.ProtocolAPI;
import net.crytec.libs.protocol.skinclient.data.Skin;
import net.crytec.libs.protocol.skinclient.data.SkinCallback;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of AvarionCore and was created at the 11.12.2019
 *
 * AvarionCore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class PlayerSkinManager {

  public PlayerSkinManager() {
    this.mineskinClient = new MineskinClient();
    this.skinMap = Maps.newHashMap();
    this.downloadRequests = new HashSet<>();
  }

  private final Set<Integer> downloadRequests;
  private final HashMap<Integer, Skin> skinMap;
  private final MineskinClient mineskinClient;

  public void cacheSkins(final File cacheFile) throws IOException {
    final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    final JsonObject json = new JsonObject();
    this.skinMap.forEach((key, value) -> {
      final JsonObject skinJson = gson.toJsonTree(value).getAsJsonObject();
      json.add("" + key, skinJson);
    });
    final OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(cacheFile));
    osw.write(gson.toJson(json));
    osw.close();
  }

  public void loadSkins(final File cacheFile) throws IOException {
    if (!cacheFile.exists()) {
      return;
    }
    final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    final InputStreamReader isr = new InputStreamReader(new FileInputStream(cacheFile));
    final StringBuilder builder = new StringBuilder();
    int read;
    while ((read = isr.read()) != -1) {
      builder.append((char) read);
    }
    isr.close();
    final JsonObject json = gson.fromJson(builder.toString(), JsonObject.class);
    for (final Entry<String, JsonElement> entry : json.entrySet()) {
      final int id = Integer.parseInt(entry.getKey());
      final Skin skin = gson.fromJson(entry.getValue(), Skin.class);
      Preconditions.checkState(skin != null);
      this.skinMap.put(id, skin);
    }
  }

  public void requestSkin(final int id, final ConsumingCallback skinCallback) {
    Skin skin = skinMap.get(id);
    if (skin != null) {
      long unixWeek = TimeUnit.DAYS.toMillis(7);
      long unixDay = unixWeek / 7;
      if (skin.downloadTimestamp + unixWeek + ThreadLocalRandom.current().nextLong(-unixDay, unixDay) < System.currentTimeMillis()) {
        ProtocolAPI.getPluginLogger().info("Skin with ID [" + id + "] has old cache data. Downloading.");
        this.mineskinClient.getSkin(id, skinCallback);
        downloadRequests.add(id);
        return;
      }
      ProtocolAPI.getPluginLogger().info("Getting Skin with ID [" + id + "] from skin cache.");
      skinCallback.skinConsumer.accept(skin);
      skinCallback.locked = false;
      return;
    }
    ProtocolAPI.getPluginLogger().info("§7Downloading Skin with ID [" + id + "] from Mineskin.org");
    downloadRequests.add(id);
    this.mineskinClient.getSkin(id, skinCallback);
  }

  public void uploadImage(final File imageFile, final String name, final ConsumingCallback skinCallback) throws IOException {
    // final BufferedImage image = ImageIO.read(imageFile);

    // Preconditions.checkArgument(image.getWidth() == 64 && image.getHeight() == 64);

    this.mineskinClient.generateUpload(imageFile, SkinOptions.create(name, Model.DEFAULT, Visibility.PRIVATE), skinCallback);
  }

  public void uploadHeadImage(final File imageFile, final String name, final ConsumingCallback skinCallback) throws IOException {
    final BufferedImage headImage = ImageIO.read(imageFile);
    Preconditions.checkArgument(headImage.getWidth() == 8 && headImage.getHeight() == 8);
    final BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
    final int[] xHeadOffsets = new int[]{8, 16, 0, 8, 16, 24};
    final int[] yHeadOffsets = new int[]{0, 0, 8, 8, 8, 8, 8};
    for (int hx = 0; hx < 8; hx++) {
      for (int hy = 0; hy < 8; hy++) {
        final int rgb = headImage.getRGB(hx, hy);
        for (int index = 0; index < 6; index++) {
          final int x = xHeadOffsets[index] + hx;
          final int y = yHeadOffsets[index] + hy;
          image.setRGB(x, y, rgb);
        }
      }
    }
    final File uploadFile = new File(imageFile.getParent(), imageFile.getName().replace(".png", "") + "_scaled.png");
    ImageIO.write(image, "png", uploadFile);
    this.uploadImage(uploadFile, name, skinCallback);
  }

  public void uploadAndScaleHeadImage(final File imageFile, final String name, final ConsumingCallback skinCallback)
      throws IOException {
    final BufferedImage headImage = ImageIO.read(imageFile);
    final double widthScale = 8D / (double) headImage.getWidth();
    final double heightScale = 8D / (double) headImage.getWidth();
    final BufferedImage image = this.scale(headImage, widthScale, heightScale);
    final File uploadFile = new File(imageFile.getParent(), imageFile.getName().replace(".png", "") + "_8.png");
    ImageIO.write(image, "png", uploadFile);
    this.uploadHeadImage(uploadFile, name, skinCallback);
  }

  private BufferedImage scale(final BufferedImage before, final double scaleWidth, final double scaleHeight) {
    final int w = before.getWidth();
    final int h = before.getHeight();
    BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    final AffineTransform at = new AffineTransform();
    at.scale(scaleWidth, scaleHeight);
    final AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
    after = scaleOp.filter(before, after);
    return after.getSubimage(0, 0, (int) (w * scaleWidth + 0.5D), ((int) (h * scaleHeight + 0.5D)));
  }

  public ConsumingCallback callback(final Consumer<Skin> skinConsumer) {
    return new ConsumingCallback(this, skinConsumer);
  }

  public static class ConsumingCallback implements SkinCallback {

    public ConsumingCallback(final PlayerSkinManager playerSkinManager, final Consumer<Skin> skinConsumer) {
      this.playerSkinManager = playerSkinManager;
      this.skinConsumer = skinConsumer;
    }

    public boolean locked = true;
    private final PlayerSkinManager playerSkinManager;
    private final Consumer<Skin> skinConsumer;

    @Override
    public void done(final Skin skin) {
      Preconditions.checkArgument(skin != null);
      if (playerSkinManager.downloadRequests.remove(skin.id)) {
        skin.downloadTimestamp = System.currentTimeMillis();
        this.playerSkinManager.skinMap.put(skin.id, skin);
      }
      this.skinConsumer.accept(skin);

      this.locked = false;
    }

    @Override
    public void uploading() {
      ProtocolAPI.getPluginLogger().info("SkinClient -> Uploading File...");
      this.locked = true;
    }

    @Override
    public void error(final String errorMessage) {
      ProtocolAPI.getPluginLogger().warning("SkinClient -> Error: " + errorMessage);
      locked = false;
    }

    @Override
    public void exception(final Exception exception) {
      exception.printStackTrace();
      locked = false;
    }

    @Override
    public void waiting(final long delay) {
      ProtocolAPI.getPluginLogger().info("SkinClient -> Waiting §e" + ((int) (delay / 100.0) / 10.0) + "s §fon connection.");
      locked = true;
    }

  }

}
