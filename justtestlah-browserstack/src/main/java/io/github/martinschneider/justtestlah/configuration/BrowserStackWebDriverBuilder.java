package io.github.martinschneider.justtestlah.configuration;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSElement;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/** Creates {@link WebDriver} instance for Browserstack. */
public class BrowserStackWebDriverBuilder extends LocalWebDriverBuilder
    implements WebDriverBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(BrowserStackWebDriverBuilder.class);

  private static final String BS_UPLOAD_PATH =
      "https://api-cloud.browserstack.com/app-automate/upload";

  @Value("${browserstack.accessKey}")
  private String accessKey;

  @Value("${browserstack.username}")
  private String username;

  @Value("${browserstack.debug}")
  private String debug;

  @Value("${browserstack.appiumLogs}")
  private String appiumLogs;

  @Value("${browserstack.video}")
  private String video;

  @Value("${browserstack.geoLocation}")
  private String geoLocation;

  @Value("${browserstack.networkProfile}")
  private String networkProfile;

  @Value("${browserstack.customNetwork}")
  private String customNetwork;

  @Value("${browserstack.timezone}")
  private String timezone;

  @Value("${browserstack.appium_version}")
  private String appiumVersion;

  private String appUrl;

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

  @Override
  protected DesiredCapabilities addCommonCapabilities(DesiredCapabilities capabilities) {
    super.addCommonCapabilities(capabilities);
    Object app = capabilities.getCapability("app");
    if (app == null) {
      throw new RuntimeException("Property app must bot be null");
    }
    if (!app.toString().startsWith("bs://")) {
      uploadAppPackage(app.toString());
      capabilities.setCapability("app", appUrl);
    } else {
      LOG.info("Using previously uploaded app package {}", app);
    }
    capabilities.setCapability("browserstack.debug", debug);
    capabilities.setCapability("browserstack.appiumLogs", appiumLogs);
    capabilities.setCapability("browserstack.video", video);
    capabilities.setCapability("browserstack.geoLocation", geoLocation);
    capabilities.setCapability("browserstack.timezone", timezone);
    capabilities.setCapability("browserstack.appium_version", appiumVersion);
    return capabilities;
  }

  private String uploadAppPackage(String path) {
    LOG.info("Uploading app package {} to Browserstack", path);
    final CloseableHttpClient httpClient = HttpClients.createSystem();
    try {
      HttpPost httpPost = new HttpPost(BS_UPLOAD_PATH);
      String encoding = Base64.getEncoder().encodeToString((username + ":" + accessKey).getBytes());
      httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
      httpPost.setEntity(
          MultipartEntityBuilder.create().addPart("file", new FileBody(new File(path))).build());
      HttpResponse response = null;
      String responseString;
      try {
        response = httpClient.execute(httpPost);
        responseString = EntityUtils.toString(response.getEntity());
      } catch (IOException exception) {
        throw new RuntimeException(
            String.format("Error uploading file to Browserstack: %s", exception.getMessage()));
      }
      if (response.getStatusLine().getStatusCode() != 200) {
        throw new RuntimeException(
            String.format(
                "Upload returned non-200 responses: %d. Check browserstack.username and browserstack.accessKey! Message: %s",
                response.getStatusLine().getStatusCode(), responseString));
      }
      try {
        String browserstackAppUrl =
            new JsonParser().parse(responseString).getAsJsonObject().get("app_url").getAsString();
        LOG.info("Successfully uploaded app package to {}", browserstackAppUrl);
        this.appiumUrl = browserstackAppUrl;
        return browserstackAppUrl;
      } catch (JsonSyntaxException | ParseException exception) {
        throw new RuntimeException("Error parsing response from Browserstack", exception);
      }
    } finally {
      try {
        httpClient.close();
      } catch (IOException exception) {
        LOG.error("Error closing HTTP client", exception);
      }
    }
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
    } catch (MalformedURLException exception) {
      LOG.error("Can't create Browserstack connection URL", exception);
      throw new RuntimeException(exception);
    }
  }
}
