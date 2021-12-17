package qa.justtestlah.configuration;

import static io.appium.java_client.remote.AndroidMobileCapabilityType.APP_ACTIVITY;
import static io.appium.java_client.remote.AndroidMobileCapabilityType.APP_PACKAGE;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.codeborne.selenide.WebDriverRunner;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import qa.justtestlah.annotations.EntryExitLogging;
import qa.justtestlah.exception.JustTestLahException;
import qa.justtestlah.log.LogLevel;
import qa.justtestlah.log.TestLogWriter;
import qa.justtestlah.log.WebDriverLogEnricher;
import qa.justtestlah.log.WebDriverServerLogEnricher;
import qa.justtestlah.mobile.tools.ApplicationInfoService;

/** Factory for {@link WebDriver}. */
@Component
@ConditionalOnProperty(value = "cloudprovider", havingValue = "local", matchIfMissing = true)
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
  
  @Value("${mobile.newCommandTimeout:60}")
  protected String newCommandTimeout;
  
  @Value("${android.adbExecTimeout:20000}")
  protected String adbExecTimeout;

  @Autowired private TestLogWriter testLog;

  private ApplicationInfoService applicationInfoService = new ApplicationInfoService();

  /*
   * (non-Javadoc)
   *
   * @see qa.justtestlah.configuration.WebDriverBuilder#getAndroidDriver()
   */
  @Override
  @EntryExitLogging(entryExitLogLevel = LogLevel.DEBUG, summaryLogLevel = LogLevel.INFO)
  public WebDriver getAndroidDriver() {
    try {
      return registerListener(
          logTestDetails(
              new AndroidDriver(
                  new URL(appiumUrl), addAndroidCapabilities(new DesiredCapabilities()))));
    } catch (MalformedURLException exception) {
      throw new JustTestLahException("Error creating Android WebDriver", exception);
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
  @EntryExitLogging(entryExitLogLevel = LogLevel.DEBUG, summaryLogLevel = LogLevel.INFO)
  public WebDriver getIOsDriver() {
    try {
      return registerListener(
          logTestDetails(
              new IOSDriver(
                  new URL(appiumUrl), addIOsCapabilities(new DesiredCapabilities()))));
    } catch (MalformedURLException exception) {
      throw new JustTestLahException("Error creating iOS WebDriver", exception);
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

  private WebDriver logTestDetails(AppiumDriver driver) {
	  // TODO: re-implement this for Appium 8
    return driver;
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
    capabilities.setCapability("deviceName", deviceName);
    capabilities.setCapability("app", appPath);
    capabilities.setCapability("platformName", platform);
    return capabilities;
  }

  protected DesiredCapabilities addIOsCapabilities(DesiredCapabilities capabilities) {
    capabilities = addCommonCapabilities(capabilities);
    capabilities = addMobileCapabilities(capabilities);
    capabilities.setCapability("automationName", "XCUITest");
    capabilities.setCapability("showXcodeLog", true);
    return capabilities;
  }

  protected DesiredCapabilities addAndroidCapabilities(DesiredCapabilities capabilities) {
    capabilities = addCommonCapabilities(capabilities);
    capabilities = addMobileCapabilities(capabilities);
    capabilities.setCapability(APP_PACKAGE, appPackage);
    capabilities.setCapability(APP_ACTIVITY, appActivity);
    capabilities.setCapability("adbExecTimeout", adbExecTimeout);
    return capabilities;
  }

  protected DesiredCapabilities addMobileCapabilities(DesiredCapabilities capabilities) {
	capabilities.setCapability("newCommandTimeout", newCommandTimeout);
    return capabilities;
  }

  @Override
  @EntryExitLogging(entryExitLogLevel = LogLevel.DEBUG, summaryLogLevel = LogLevel.INFO)
  public WebDriver getWebDriver() {
    return registerListener(WebDriverRunner.getWebDriver());
  }

  private WebDriver registerListener(WebDriver driver) {
    EventFiringWebDriver eventFiringDriver = new EventFiringWebDriver(driver);
    eventFiringDriver.register(new WebDriverLogEnricher());
    eventFiringDriver.register(new WebDriverServerLogEnricher());
    return eventFiringDriver;
  }
}
