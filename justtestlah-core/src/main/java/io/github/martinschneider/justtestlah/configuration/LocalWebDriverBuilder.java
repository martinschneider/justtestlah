package io.github.martinschneider.justtestlah.configuration;

import static io.appium.java_client.remote.AndroidMobileCapabilityType.APP_ACTIVITY;
import static io.appium.java_client.remote.AndroidMobileCapabilityType.APP_PACKAGE;
import com.codeborne.selenide.WebDriverRunner;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSElement;
import java.net.MalformedURLException;
import java.net.URL;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Value;

/**
 * Factory for {@link WebDriver}.
 */
public class LocalWebDriverBuilder implements WebDriverBuilder {

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
      return new AppiumDriver<AndroidElement>(new URL(appiumUrl),
          addAndroidCapabilities(new DesiredCapabilities()));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
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
      return new AppiumDriver<IOSElement>(new URL(appiumUrl),
          addIOsCapabilities(new DesiredCapabilities()));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
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
