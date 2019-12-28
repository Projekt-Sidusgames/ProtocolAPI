package net.crytec.libs.protocol.holograms.infobars;

public enum InfoLineSpacing {

  SMALL(0.12),
  MEDIUM(0.25),
  LARGE(0.45);

  private final double spacingValue;

  private InfoLineSpacing(final double spacingValue) {
    this.spacingValue = spacingValue;
  }

  public double getSpacingValue() {
    return this.spacingValue;
  }

}
