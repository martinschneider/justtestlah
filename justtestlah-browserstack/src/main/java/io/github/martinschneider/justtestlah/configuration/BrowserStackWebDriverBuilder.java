package io.github.martinschneider.justtestlah.configuration;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSElement;
import java.net.MalformedURLException;
import java.net.URL;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

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
   * @see io.github.martinschneider.justtestlah.configuration.WebDriverBuilder# getAndroidDriver()
   */
  @Override
  public WebDriver getAndroidDriver() {
    return new AppiumDriver<AndroidElement>(
        buildBrowserStackUrl(accessKey, username),
        addAndroidCapabilities(new DesiredCapabilities()));
  }

  /*
   * (non-Javadoc)
   *
   * @see io.github.martinschneider.justtestlah.configuration.WebDriverBuilder#getIOSDriver()
   */
  @Override
  public WebDriver getIOsDriver() {
    return new AppiumDriver<IOSElement>(
        buildBrowserStackUrl(accessKey, username), addIOsCapabilities(new DesiredCapabilities()));
  }

  protected DesiredCapabilities addCommonCapabilities(DesiredCapabilities capabilities) {
    super.addCommonCapabilities(capabilities);
    capabilities.setCapability("browserstack.debug", debug);
    return capabilities;
  }

  /*
   * (non-Javadoc)
   *
   * @see io.github.martinschneider.justtestlah.configuration.WebDriverBuilder#getWebDriver()
   */
  @Override
  public WebDriver getWebDriver() {
    throw new UnsupportedOperationException(
        "For Browserstack only mobile testing is supported at the moment.");
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
