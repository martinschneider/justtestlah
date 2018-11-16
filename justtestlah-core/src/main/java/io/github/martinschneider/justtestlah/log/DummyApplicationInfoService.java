package io.github.martinschneider.justtestlah.log;

public class DummyApplicationInfoService extends ApplicationInfoService {

  private static final String UNKNOWN = "unknown";

  @Override
  protected ApplicationInfo getAppInfo(String platform, String appPath) {
    return new ApplicationInfo(UNKNOWN, UNKNOWN, UNKNOWN);
  }
}
