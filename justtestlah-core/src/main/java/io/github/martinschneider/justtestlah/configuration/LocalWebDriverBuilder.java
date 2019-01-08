package io.github.martinschneider.justtestlah.configuration;

import static io.appium.java_client.remote.AndroidMobileCapabilityType.APP_ACTIVITY;
import static io.appium.java_client.remote.AndroidMobileCapabilityType.APP_PACKAGE;

import com.codeborne.selenide.WebDriverRunner;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/** Factory for {@link WebDriver}. */
public class LocalWebDriverBuilder implements WebDriverBuilder {

  private static final String EXIT_ON_WEB_DRIVER_INITIALISATION_ERROR =
      "exitOnWebDriverInitialisationError";

  private Logger LOG = LoggerFactory.getLogger(LocalWebDriverBuilder.class);

  @Value("${platform}")
  protected String platform;

  @Value("#{'${platform}'=='android' ? '${android.appPath}' : '${ios.appPath}'}")
  protected String appPath;

  @Value("#{'${platform}'=='android' ? '${android.deviceName}' : '${ios.deviceName}'}")
  protected String deviceName;

  @Value("${android.appPackage}")
  protected String appPackage;

  @Value("${android.appActivity}")
  protected String appActivity;

  @Value("${mobile.appiumUrl}")
  protected String appiumUrl;

  /*
   * (non-Javadoc)
   *
   * @see io.github.martinschneider.justtestlah.configuration.WebDriverBuilder#getAndroidDriver()
   */
  @Override
  public WebDriver getAndroidDriver() {
    try {
      return new AndroidDriver<AndroidElement>(
          new URL(appiumUrl), addAndroidCapabilities(new DesiredCapabilities()));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    } catch (WebDriverException e) {
      LOG.error("Error creating web driver", e);
      LOG.error("Appium server error: {}", getServerError(e));
      if (e.getMessage().contains("Connection refused")) {
        LOG.error("Check whether Appium is running!");
      }
      if (Boolean.parseBoolean(System.getProperty(EXIT_ON_WEB_DRIVER_INITIALISATION_ERROR))) {
        LOG.error("Error during Webdriver initialisation. Exiting.");
        System.exit(1);
      }
      return null;
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see io.github.martinschneider.justtestlah.configuration.WebDriverBuilder#getIOSDriver()
   */
  @Override
  public WebDriver getIOsDriver() {
    try {
      return new IOSDriver<IOSElement>(
          new URL(appiumUrl), addIOsCapabilities(new DesiredCapabilities()));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    } catch (WebDriverException e) {
      LOG.error("Error creating web driver", e);
      LOG.error("Appium server error: {}", getServerError(e));
      if (e.getMessage().contains("Connection refused")) {
        LOG.error("Check whether Appium is running!");
      }
      if (Boolean.parseBoolean(System.getProperty(EXIT_ON_WEB_DRIVER_INITIALISATION_ERROR))) {
        LOG.error("Error during Webdriver initialisation. Exiting.");
        System.exit(1);
      }
      return null;
    }
  }

  private String getServerError(Throwable e) {
    while (e != null) {
      if (e.getMessage() != null && e.getMessage().contains("remote stacktrace:")) {
        Matcher m = Pattern.compile("remote stacktrace:(.*)\\n").matcher(e.getMessage());
        if (m.find()) {
          return m.group(1).trim();
        }
      }
      e = e.getCause();
    }
    return null;
  }

  protected DesiredCapabilities addCommonCapabilities(DesiredCapabilities capabilities) {
    capabilities.setCapability("newCommandTimeout", 600);
    capabilities.setCapability("launchTimeout", 90000);
    capabilities.setCapability("deviceName", deviceName);
    capabilities.setCapability("app", appPath);
    capabilities.setCapability("platformName", platform);
    return capabilities;
  }

  protected DesiredCapabilities addIOsCapabilities(DesiredCapabilities capabilities) {
    capabilities = addCommonCapabilities(capabilities);
    capabilities.setCapability("automationName", "XCUITest");
    capabilities.setCapability("showXcodeLog", true);
    return capabilities;
  }

  protected DesiredCapabilities addAndroidCapabilities(DesiredCapabilities capabilities) {
    capabilities = addCommonCapabilities(capabilities);
    capabilities.setCapability(APP_PACKAGE, appPackage);
    capabilities.setCapability(APP_ACTIVITY, appActivity);
    return capabilities;
  }

  @Override
  public WebDriver getWebDriver() {
    return WebDriverRunner.getWebDriver();
  }
}
