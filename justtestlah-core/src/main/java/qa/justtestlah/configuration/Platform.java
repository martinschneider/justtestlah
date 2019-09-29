package qa.justtestlah.configuration;

/** Enum for platforms. */
public enum Platform {
  ANDROID(Constants.ANDROID),
  IOS(Constants.IOS),
  WEB(Constants.WEB),
  DEFAULT(Constants.WEB);

  private String platformName;

  Platform(String platformName) {
    this.platformName = platformName;
  }

  public String getPlatformName() {
    return platformName;
  }

  public static class Constants {
    public static final String WEB = "web";
    public static final String IOS = "ios";
    public static final String ANDROID = "android";
  }
}
