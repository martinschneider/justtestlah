package io.github.martinschneider.yasew.configuration;

import static io.appium.java_client.remote.AndroidMobileCapabilityType.APP_ACTIVITY;
import static io.appium.java_client.remote.AndroidMobileCapabilityType.APP_PACKAGE;
import java.net.MalformedURLException;
import java.net.URL;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSElement;

public class BrowserStackWebDriverBuilder extends LocalWebDriverBuilder
    implements WebDriverBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(BrowserStackWebDriverBuilder.class);

  @Value("${browserstack.accessKey}")
  private String accessKey;

  @Value("${browserstack.username}")
  private String username;

  @Value("${browserstack.debug}")
  private String debug;

  /*
   * (non-Javadoc)
   *
   * @see io.github.martinschneider.yasew.configuration.WebDriverBuilder#
   * getAndroidDriver()
   */
  @Override
  public WebDriver getAndroidDriver() {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setCapability("deviceName", androidDeviceName);
    capabilities.setCapability("app", androidAppPath);
    capabilities.setCapability(APP_PACKAGE, appPackage);
    capabilities.setCapability(APP_ACTIVITY, appActivity);
    capabilities.setCapability("platformName", "android");
    capabilities.setCapability("browserstack.debug", debug);
    return new AppiumDriver<AndroidElement>(
        buildBrowserStackUrl(accessKey, username), capabilities);
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * io.github.martinschneider.yasew.configuration.WebDriverBuilder#getIOSDriver()
   */
  @Override
  public WebDriver getIOSDriver() {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setCapability("deviceName", iosDeviceName);
    capabilities.setCapability("app", iosAppPath);
    capabilities.setCapability("platformName", "iOS");
    capabilities.setCapability("automationName", "XCUITest");
    capabilities.setCapability("showXcodeLog", true);
    capabilities.setCapability("startIWDP", true);
    capabilities.setCapability("browserstack.debug", debug);
    return new AppiumDriver<IOSElement>(buildBrowserStackUrl(accessKey, username), capabilities);
  }

  private URL buildBrowserStackUrl(String accessKey, String username) {
    try {
      return new URL("http://" + username + ":" + accessKey + "@hub-cloud.browserstack.com/wd/hub");
    } catch (MalformedURLException e) {
      LOG.error("Can't create Browserstack connection URL", e);
      throw new RuntimeException(e);
    }
  }
}
