package io.github.martinschneider.justtestlah.configuration;

import com.ssts.pcloudy.Connector;
import com.ssts.pcloudy.appium.PCloudyAppiumSession;
import com.ssts.pcloudy.dto.appium.booking.BookingDtoDevice;
import com.ssts.pcloudy.dto.device.MobileDevice;
import com.ssts.pcloudy.dto.file.PDriveFileDTO;
import com.ssts.pcloudy.exception.ConnectError;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class PCloudyWebDriverBuilder extends LocalWebDriverBuilder implements WebDriverBuilder {

  private static final String PCLOUDY_API_URL = "https://device.pcloudy.com/api/";

  private static final Logger LOG = LoggerFactory.getLogger(PCloudyWebDriverBuilder.class);

  @Value("${pcloudy.email}")
  private String email;

  @Value("${pcloudy.apikey}")
  private String apiKey;

  @Value("${pcloudy.duration}")
  private Integer duration;

  /*
   * (non-Javadoc)
   *
   * @see io.github.martinschneider.justtestlah.configuration.WebDriverBuilder# getAndroidDriver()
   */
  @Override
  public WebDriver getAndroidDriver() {
    PCloudyAppiumSession pCloudySession;
    try {
      pCloudySession = getPCloudySession();
    } catch (IOException | ConnectError | InterruptedException exception) {
      LOG.error("Error creating pCloudy session!", exception);
      return null;
    }
    return new AndroidDriver<AndroidElement>(
        buildPCloudyUrl(pCloudySession),
        addPCloudyCapabilities(
            addAndroidCapabilities(new DesiredCapabilities()), pCloudySession.getDto()));
  }

  /*
   * (non-Javadoc)
   *
   * @see io.github.martinschneider.justtestlah.configuration.WebDriverBuilder#getIOSDriver()
   */
  @Override
  public WebDriver getIOsDriver() {
    PCloudyAppiumSession pCloudySession;
    try {
      pCloudySession = getPCloudySession();
    } catch (IOException | ConnectError | InterruptedException exception) {
      LOG.error("Error creating pCloudy session!", exception);
      return null;
    }
    return new IOSDriver<IOSElement>(
        buildPCloudyUrl(pCloudySession),
        addPCloudyCapabilities(
            addIOsCapabilities(new DesiredCapabilities()), pCloudySession.getDto()));
  }

  private Capabilities addPCloudyCapabilities(
      DesiredCapabilities capabilities, BookingDtoDevice device) {
    capabilities.setCapability("deviceName", device.capabilities.deviceName);
    capabilities.setCapability("browserName", device.capabilities.browserName);
    capabilities.setCapability("platformName", device.capabilities.platformName);
    capabilities.setCapability("app", "");
    return capabilities;
  }

  private URL buildPCloudyUrl(PCloudyAppiumSession pCloudySession) {
    try {
      return pCloudySession
          .getConnector()
          .AppiumApis()
          .getAppiumEndpoint(pCloudySession.getAuthToken());
    } catch (IOException | ConnectError | InterruptedException exception) {
      LOG.error("Error building pCloudy Appium URL!", exception);
      return null;
    }
  }

  private PCloudyAppiumSession getPCloudySession()
      throws IOException, ConnectError, InterruptedException {
    Connector con = new Connector(PCLOUDY_API_URL);
    String authToken = con.authenticateUser(email, apiKey);
    File fileToBeUploaded = new File(appPath);
    PDriveFileDTO alreadyUploadedApp =
        con.getAvailableAppIfUploaded(authToken, fileToBeUploaded.getName());
    if (alreadyUploadedApp == null) {
      LOG.info("Uploading App: {} ", fileToBeUploaded.getAbsolutePath());
      PDriveFileDTO uploadedApp = con.uploadApp(authToken, fileToBeUploaded, false);
      LOG.info("App uploaded");
      alreadyUploadedApp = new PDriveFileDTO();
      alreadyUploadedApp.file = uploadedApp.file;
    } else {
      LOG.info("App already present. Not uploading... ");
    }
    ArrayList<MobileDevice> selectedDevices = new ArrayList<MobileDevice>();
    for (MobileDevice device : con.getDevices(authToken, 10, platform, true)) {
      if (device.display_name.contains(deviceName)) {
        selectedDevices.add(device);
        break;
      }
    }
    if (selectedDevices.size() == 0) {
      LOG.error("No device of type {} available for testing", deviceName);
      return null;
    }
    LOG.info("Booking device {} for {} minutes", selectedDevices.get(0).display_name, duration);
    String sessionName = "Appium Session " + new Date();
    BookingDtoDevice bookedDevice =
        con.AppiumApis().bookDevicesForAppium(authToken, selectedDevices, duration, sessionName)[0];
    LOG.info("Booking successful. Session name: {}", sessionName);
    con.AppiumApis().initAppiumHubForApp(authToken, alreadyUploadedApp);
    return new PCloudyAppiumSession(con, authToken, bookedDevice);
  }
}
