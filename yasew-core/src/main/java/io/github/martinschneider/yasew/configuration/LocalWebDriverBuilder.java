package io.github.martinschneider.yasew.configuration;

import static io.appium.java_client.remote.AndroidMobileCapabilityType.APP_ACTIVITY;
import static io.appium.java_client.remote.AndroidMobileCapabilityType.APP_PACKAGE;
import java.net.MalformedURLException;
import java.net.URL;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Value;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSElement;

/** Factory for {@link WebDriver} */
public class LocalWebDriverBuilder implements WebDriverBuilder {

  @Value("${android.deviceName}")
  protected String androidDeviceName;

  @Value("${ios.deviceName}")
  protected String iosDeviceName;

  @Value("${android.appPath}")
  protected String androidAppPath;

  @Value("${ios.appPath}")
  protected String iosAppPath;

  @Value("${android.appPackage}")
  protected String appPackage;

  @Value("${android.appActivity}")
  protected String appActivity;

  @Value("${mobile.appiumUrl}")
  protected String appiumUrl;

  /* (non-Javadoc)
   * @see io.github.martinschneider.yasew.configuration.WebDriverBuilder#getAndroidDriver()
   */
  @Override
  public WebDriver getAndroidDriver() {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setCapability("deviceName", androidDeviceName);
    capabilities.setCapability("app", androidAppPath);
    capabilities.setCapability(APP_PACKAGE, appPackage);
    capabilities.setCapability(APP_ACTIVITY, appActivity);
    capabilities.setCapability("platformName", "android");
    try {
      return new AppiumDriver<AndroidElement>(new URL(appiumUrl), capabilities);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  /* (non-Javadoc)
   * @see io.github.martinschneider.yasew.configuration.WebDriverBuilder#getIOSDriver()
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
    try {
      return new AppiumDriver<IOSElement>(new URL(appiumUrl), capabilities);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}
