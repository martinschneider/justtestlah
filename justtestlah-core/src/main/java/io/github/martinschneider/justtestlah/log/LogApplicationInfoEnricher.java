package io.github.martinschneider.justtestlah.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import io.github.martinschneider.justtestlah.junit.JustTestLahRunner;
import io.github.martinschneider.justtestlah.junit.JustTestLahTest;
import io.github.martinschneider.justtestlah.mobile.tools.ApplicationInfoService;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/** Enrich meta information about the application under test to the log file. */
public class LogApplicationInfoEnricher extends ContextAwareBase
    implements LoggerContextListener, LifeCycle {

  private boolean started = false;

  @Override
  public void start() {
    if (started) {
      return;
    }
    Properties props = getProperties();
    Context context = getContext();
    String platform = props.getProperty("platform");
    String appPath = props.getProperty(platform + ".appPath");

    StringBuilder strBuilder = new StringBuilder(platform.toUpperCase());
    if (platform.equalsIgnoreCase("android") || platform.equalsIgnoreCase("ios")) {
      String appInfo = new ApplicationInfoService().getAppInfo(appPath).toString();
      if (appInfo != null && !appInfo.isEmpty()) {
        strBuilder.append(" ");
        strBuilder.append(appInfo);
      }
    }
    context.putProperty("appInfo", strBuilder.toString());
    started = true;
  }

  private Properties getProperties() {
    Properties props = new Properties();
    String propertiesLocation = System.getProperty(JustTestLahRunner.JUST_TEST_LAH_LOCATION_KEY);
    try {
      if (propertiesLocation != null) {
        props.load(new FileInputStream(propertiesLocation));
      } else {
        propertiesLocation = JustTestLahRunner.DEFAULT_JUST_TEST_LAH_PROPERTIES;
        props.load(JustTestLahTest.class.getClassLoader().getResourceAsStream(propertiesLocation));
      }
    } catch (NullPointerException | IOException e) {
    }
    return props;
  }

  @Override
  public void stop() {}

  @Override
  public boolean isStarted() {
    return started;
  }

  @Override
  public boolean isResetResistant() {
    return true;
  }

  @Override
  public void onStart(LoggerContext context) {}

  @Override
  public void onReset(LoggerContext context) {}

  @Override
  public void onStop(LoggerContext context) {}

  @Override
  public void onLevelChange(Logger logger, Level level) {}
}
