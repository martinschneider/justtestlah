package qa.justtestlah.configuration;

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
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import qa.justtestlah.annotations.EntryExitLogging;
import qa.justtestlah.log.WebDriverLogEnricher;

/** Factory for {@link WebDriver}. */
public class LocalWebDriverBuilder implements WebDriverBuilder {

  private static final String EXIT_ON_WEB_DRIVER_INITIALISATION_ERROR =
      "exitOnWebDriverInitialisationError";

  private static final Logger LOG = LoggerFactory.getLogger(LocalWebDriverBuilder.class);

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

  @Value("${mobile.deviceOrientation}")
  protected String deviceOrientation;

  /*
   * (non-Javadoc)
   *
   * @see qa.justtestlah.configuration.WebDriverBuilder#getAndroidDriver()
   */
  @Override
  @EntryExitLogging
  public WebDriver getAndroidDriver() {
    try {
      return registerListener(
          new AndroidDriver<AndroidElement>(
              new URL(appiumUrl), addAndroidCapabilities(new DesiredCapabilities())));
    } catch (MalformedURLException exception) {
      throw new RuntimeException(exception);
    } catch (WebDriverException exception) {
      LOG.error("Error creating web driver", exception);
      LOG.error("Appium server error: {}", getServerError(exception));
      if (exception.getMessage().contains("Connection refused")) {
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
   * @see qa.justtestlah.configuration.WebDriverBuilder#getIOSDriver()
   */
  @Override
  public WebDriver getIOsDriver() {
    try {
      return registerListener(
          new IOSDriver<IOSElement>(
              new URL(appiumUrl), addIOsCapabilities(new DesiredCapabilities())));
    } catch (MalformedURLException exception) {
      throw new RuntimeException(exception);
    } catch (WebDriverException exception) {
      LOG.error("Error creating web driver", exception);
      LOG.error("Appium server error: {}", getServerError(exception));
      if (exception.getMessage().contains("Connection refused")) {
        LOG.error("Check whether Appium is running!");
      }
      if (Boolean.parseBoolean(System.getProperty(EXIT_ON_WEB_DRIVER_INITIALISATION_ERROR))) {
        LOG.error("Error during Webdriver initialisation. Exiting.");
        System.exit(1);
      }
      return null;
    }
  }

  private String getServerError(Throwable exception) {
    while (exception != null) {
      if (exception.getMessage() != null && exception.getMessage().contains("remote stacktrace:")) {
        Matcher m = Pattern.compile("remote stacktrace:(.*)\\n").matcher(exception.getMessage());
        if (m.find()) {
          return m.group(1).trim();
        }
      }
      exception = exception.getCause();
    }
    return null;
  }

  protected DesiredCapabilities addCommonCapabilities(DesiredCapabilities capabilities) {
    capabilities.setCapability("newCommandTimeout", 600);
    capabilities.setCapability("launchTimeout", 90000);
    capabilities.setCapability("deviceName", deviceName);
    capabilities.setCapability("app", appPath);
    capabilities.setCapability("platformName", platform);
    capabilities.setCapability("deviceOrientation", deviceOrientation);
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
    return registerListener(WebDriverRunner.getWebDriver());
  }

  private WebDriver registerListener(WebDriver driver) {
    EventFiringWebDriver eventFiringDriver = new EventFiringWebDriver(driver);
    eventFiringDriver.register(new WebDriverLogEnricher());
    return eventFiringDriver;
  }
}
