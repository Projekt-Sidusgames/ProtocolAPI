package net.crytec.libs.protocol.skinclient;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
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
    this.skinMap = new Int2ObjectOpenHashMap<>();
  }

  private final Int2ObjectMap<Skin> skinMap;
  private final MineskinClient mineskinClient;

  public void cacheSkins(File cacheFile) throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    JsonObject json = new JsonObject();
    skinMap.int2ObjectEntrySet().forEach(entry -> {
      Object src;
      JsonObject skinJson = gson.toJsonTree(entry.getValue()).getAsJsonObject();
      json.add("" + entry.getIntKey(), skinJson);
    });
    OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(cacheFile));
    osw.write(gson.toJson(json));
    osw.close();
  }

  public void loadSkins(File cacheFile) throws IOException {
    if (!cacheFile.exists()) {
      return;
    }
    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    InputStreamReader isr = new InputStreamReader(new FileInputStream(cacheFile));
    StringBuilder builder = new StringBuilder();
    int read;
    while ((read = isr.read()) != -1) {
      builder.append((char) read);
    }
    isr.close();
    JsonObject json = gson.fromJson(builder.toString(), JsonObject.class);
    for (Entry<String, JsonElement> entry : json.entrySet()) {
      int id = Integer.parseInt(entry.getKey());
      Skin skin = gson.fromJson(entry.getValue(), Skin.class);
      Preconditions.checkState(skin != null);
      this.skinMap.put(id, skin);
    }
  }

  public void requestSkin(final int id, final ConsumingCallback skinCallback) {
    if (this.skinMap.containsKey(id)) {
      skinCallback.skinConsumer.accept(this.skinMap.get(id));
    }
    this.mineskinClient.getSkin(id, skinCallback);
  }

  public void uploadImage(final File imageFile, final String name, final ConsumingCallback skinCallback) throws IOException {
    final BufferedImage image = ImageIO.read(imageFile);
    Preconditions.checkArgument(image.getWidth() == 64 && image.getHeight() == 64);

    this.mineskinClient.generateUpload(imageFile, SkinOptions.create(name, Model.DEFAULT, Visibility.PRIVATE), skinCallback);
  }

  public void uploadHeadImage(final File imageFile, final String name, ConsumingCallback skinCallback) throws IOException {
    final BufferedImage headImage = ImageIO.read(imageFile);
    Preconditions.checkArgument(headImage.getWidth() == 8 && headImage.getHeight() == 8);
    final BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
    int[] xHeadOffsets = new int[]{8, 16, 0, 8, 16, 24};
    int[] yHeadOffsets = new int[]{0, 0, 8, 8, 8, 8, 8};
    for (int hx = 0; hx < 8; hx++) {
      for (int hy = 0; hy < 8; hy++) {
        int rgb = headImage.getRGB(hx, hy);
        for (int index = 0; index < 6; index++) {
          int x = xHeadOffsets[index] + hx;
          int y = yHeadOffsets[index] + hy;
          image.setRGB(x, y, rgb);
        }
      }
    }
    File uploadFile = new File(imageFile.getParent(), imageFile.getName().replace(".png", "") + "_scaled.png");
    ImageIO.write(image, "png", uploadFile);
    uploadImage(uploadFile, name, skinCallback);
  }

  public void uploadAndScaleHeadImage(final File imageFile, final String name, ConsumingCallback skinCallback)
      throws IOException {
    final BufferedImage headImage = ImageIO.read(imageFile);
    double widthScale = 8D / (double) headImage.getWidth();
    double heaightScale = 8D / (double) headImage.getWidth();
    final BufferedImage image = scale(headImage, widthScale, heaightScale);
    File uploadFile = new File(imageFile.getParent(), imageFile.getName().replace(".png", "") + "_8.png");
    ImageIO.write(image, "png", uploadFile);
    uploadHeadImage(uploadFile, name, skinCallback);
  }

  private BufferedImage scale(BufferedImage before, double scaleWidth, double scaleHeight) {
    int w = before.getWidth();
    int h = before.getHeight();
    BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    AffineTransform at = new AffineTransform();
    at.scale(scaleWidth, scaleHeight);
    AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
    after = scaleOp.filter(before, after);
    return after.getSubimage(0, 0, (int) (w * scaleWidth + 0.5D), ((int) (h * scaleHeight + 0.5D)));
  }

  public ConsumingCallback callback(Consumer<Skin> skinConsumer) {
    return new ConsumingCallback(this, skinConsumer);
  }

  public static class ConsumingCallback implements SkinCallback {

    public ConsumingCallback(PlayerSkinManager playerSkinManager, Consumer<Skin> skinConsumer) {
      this.playerSkinManager = playerSkinManager;
      this.skinConsumer = skinConsumer;
    }

    public boolean locked = true;
    private final PlayerSkinManager playerSkinManager;
    private final Consumer<Skin> skinConsumer;

    @Override
    public void done(final Skin skin) {
      Preconditions.checkArgument(skin != null);
      playerSkinManager.skinMap.put(skin.id, skin);
      this.skinConsumer.accept(skin);

      locked = false;
    }

    @Override
    public void uploading() {
      System.out.println("§e[MineskinClient]§f Uploading File...");
      locked = false;
    }

    @Override
    public void error(String errorMessage) {
      System.out.println("§e[MineskinClient]§c Error: " + errorMessage);
    }

    @Override
    public void exception(Exception exception) {
      exception.printStackTrace();
    }

    @Override
    public void waiting(long delay) {
      System.out.println("§e[MineskinClient]§f Waiting §e" + (delay + 1000) + "ms §fon connection.");
    }

  }

}
