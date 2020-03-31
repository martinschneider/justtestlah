package qa.justtestlah.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import qa.justtestlah.configuration.PropertiesHolder;
import qa.justtestlah.mobile.tools.ApplicationInfo;
import qa.justtestlah.mobile.tools.ApplicationInfoService;

/** Enrich meta information about the application under test to the log file. */
@Deprecated(forRemoval = true)
public class ApplicationInfoEnricher extends ContextAwareBase
    implements LoggerContextListener, LifeCycle {

  private boolean started = false;

  private ApplicationInfoService applicationInfoService = new ApplicationInfoService();

  private PropertiesHolder props = new PropertiesHolder();

  @Override
  public void start() {
    if (started) {
      return;
    }
    props.getProperties();
    Context context = getContext();
    String platform = props.getProperty("platform");

    StringBuilder strBuilder = new StringBuilder(platform.toUpperCase());
    if (platform.equalsIgnoreCase("android") || platform.equalsIgnoreCase("ios")) {
      String appPath = props.getProperty(platform + ".appPath");
      ApplicationInfo appInfo = applicationInfoService.getAppInfo(appPath);
      if (appInfo != null && !appInfo.toString().isEmpty()) {
        strBuilder.append(" ");
        strBuilder.append(appInfo);
      }
    }
    context.putProperty("appInfo", strBuilder.toString());
    started = true;
  }

  @Override
  public void stop() {
    // do nothing
  }

  @Override
  public boolean isStarted() {
    return started;
  }

  @Override
  public boolean isResetResistant() {
    return true;
  }

  @Override
  public void onStart(LoggerContext context) {
    // do nothing
  }

  @Override
  public void onReset(LoggerContext context) {
    // do nothing
  }

  @Override
  public void onStop(LoggerContext context) {
    // do nothing
  }

  @Override
  public void onLevelChange(Logger logger, Level level) {
    // do nothing
  }

  protected void setApplicationInfoService(ApplicationInfoService applicationInfoService) {
    this.applicationInfoService = applicationInfoService;
  }
}
