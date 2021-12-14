package qa.justtestlah.configuration;

import static com.codeborne.selenide.Selenide.open;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import qa.justtestlah.mobile.tools.ApplicationInfo;
import qa.justtestlah.mobile.tools.ApplicationInfoService;
import qa.justtestlah.stubs.OCR;

/** Main configuration class for JustTestLah!. */
@Component
public class JustTestLahConfiguration {

  private static final String DEFAULT_BROWSER = "chrome";
  private static final String DEFAULT_FEATURE_PATH = "src/test/resources/features";
  private static final String DEFAULT_GALEN_REPORT_DIRECTORY = "target/galen-reports";

  @Value("${web.browser:" + DEFAULT_BROWSER + "}")
  private String browser;

  @Value("${web.baseUrl}")
  private String baseUrl;

  @Value("${web.headless:false}")
  private boolean headless;

  @Value("${features.path:" + DEFAULT_FEATURE_PATH + "}")
  private String featuresPath;

  @Value("${steps.package}")
  private String stepsPackage;

  @Value("${application.name}")
  private String applicationName;

  @Value("${platform}")
  private String platformString;

  @Value("${android.appPath}")
  private String androidAppPath;

  @Value("${ios.appPath}")
  private String iosAppPath;

  @Value("${opencv.mode:client}")
  private String openCVMode;

  @Value("${eyes.enabled:false}")
  private boolean eyesEnabled;

  @Value("${galen.enabled:false}")
  private boolean galenEnabled;

  @Value("${galen.report.directory:" + DEFAULT_GALEN_REPORT_DIRECTORY + "}")
  private String galenReportDirectory;

  @Value("${cloudprovider:local}")
  private String cloudProvider;

  private String applicationInfo;

  private WebDriverBuilder webDriverBuilder;

  @Autowired private OCR ocr;

  private ApplicationInfoService applicationInfoService = new ApplicationInfoService();

  @Autowired
  public JustTestLahConfiguration(WebDriverBuilder webDriverBuilder) {
    this.webDriverBuilder = webDriverBuilder;
  }

  /** Set the correct {@link org.openqa.selenium.WebDriver}. */
  public synchronized void initWebDriver() {
    // for web and local testing the Selenide default behavior is sufficient
    System.setProperty("browser", browser);
    // not thread-safe!
    //Configuration.headless = headless;
    Configuration.browser = "opera";
    Platform platform = getPlatform();
    if (platform.equals(Platform.ANDROID)) {
      WebDriverRunner.setWebDriver(webDriverBuilder.getAndroidDriver());
    } else if (platform.equals(Platform.IOS)) {
      WebDriverRunner.setWebDriver(webDriverBuilder.getIOsDriver());
    } else if (platform.equals(Platform.WEB)) {
      if (cloudProvider.equals("browserstack")) {
        WebDriverRunner.setWebDriver(webDriverBuilder.getWebDriver());
        open(baseUrl);
      } else {
        open(baseUrl);
        WebDriverRunner.setWebDriver(webDriverBuilder.getWebDriver());
      }
    }
    WebDriver driver = WebDriverRunner.getWebDriver();
    if (driver instanceof TakesScreenshot) {
      ocr.setDriver(driver);
    }
  }

  /**
   * Gets the base URL of the application under test.
   *
   * @return the base URL of the application under test
   */
  public String getBaseUrl() {
    if (!getPlatform().equals(Platform.WEB)) {
      throw new UnsupportedOperationException("baseUrl is only available for platform web");
    }
    return baseUrl;
  }

  /**
   * Get the platform to test against.
   *
   * @return the platform to test against
   */
  public Platform getPlatform() {
    return Platform.valueOf(platformString.toUpperCase());
  }

  public String getApplicationName() {
    return applicationName;
  }

  public String getFeaturesPath() {
    return featuresPath;
  }

  public boolean isEyesEnabled() {
    return eyesEnabled;
  }

  public boolean isGalenEnabled() {
    return galenEnabled;
  }

  public boolean isOpenCvEnabled() {
    return "client".equals(openCVMode);
  }

  public String getGalenReportDirectory() {
    return galenReportDirectory;
  }

  public ExecutionEnvironment getExecutionEnvironment() {
    return ExecutionEnvironment.valueOf(cloudProvider.toUpperCase());
  }

  /** @return application info String */
  public String getApplicationInfo() {
    if (applicationInfo != null) {
      return applicationInfo;
    }
    StringBuilder strBuilder = new StringBuilder(platformString.toUpperCase());
    String appPath = null;
    if (platformString.equalsIgnoreCase("android")) {
      appPath = androidAppPath;
    } else if (platformString.equalsIgnoreCase("ios")) {
      appPath = iosAppPath;
    }
    ApplicationInfo appInfo = applicationInfoService.getAppInfo(appPath);
    if (appInfo != null && !appInfo.toString().isEmpty()) {
      strBuilder.append(" ");
      strBuilder.append(appInfo);
    }
    applicationInfo = strBuilder.toString();
    return applicationInfo;
  }
}
