package qa.justtestlah.configuration;

import static com.codeborne.selenide.Selenide.open;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

  private WebDriverBuilder webDriverBuilder;

  @Autowired
  public JustTestLahConfiguration(WebDriverBuilder webDriverBuilder) {
    this.webDriverBuilder = webDriverBuilder;
  }

  /** Set the correct {@link org.openqa.selenium.WebDriver}. */
  public synchronized void initWebDriver() {
    // for web and local testing the Selenide default behavior is sufficient
    System.setProperty("browser", browser);
    // not thread-safe!
    Configuration.headless = headless;
    Platform platform = getPlatform();
    if (platform.equals(Platform.ANDROID)) {
      WebDriverRunner.setWebDriver(webDriverBuilder.getAndroidDriver());
    } else if (platform.equals(Platform.IOS)) {
      WebDriverRunner.setWebDriver(webDriverBuilder.getIOsDriver());
    } else if (platform.equals(Platform.WEB)) {
      open(baseUrl);
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
    return openCVMode.equals("client");
  }

  public String getGalenReportDirectory() {
    return galenReportDirectory;
  }

  public ExecutionEnvironment getExecutionEnvironment() {
    return ExecutionEnvironment.valueOf(cloudProvider.toUpperCase());
  }
}
