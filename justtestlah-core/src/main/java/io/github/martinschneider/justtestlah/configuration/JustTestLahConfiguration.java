package io.github.martinschneider.justtestlah.configuration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import io.github.martinschneider.justtestlah.user.UserService;
import javax.annotation.PostConstruct;
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
  private String platform;

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

  private UserService userService;

  @Autowired
  public JustTestLahConfiguration(WebDriverBuilder webDriverBuilder, UserService userService) {
    this.webDriverBuilder = webDriverBuilder;
    this.userService = userService;
  }

  /** Initialise the user service. */
  @PostConstruct
  public void initCucumberConfig() {
    userService.initialize();
  }

  /** Set the correct {@link org.openqa.selenium.WebDriver}. */
  public void initWebDriver() {
    // for web and local testing the Selenide default behavior is sufficient
    System.setProperty("browser", browser);
    Configuration.headless = headless;
    // for Android and IOS we construct the {@link WebDriver} ourselves
    if (platform.equals(Platform.ANDROID)) {
      WebDriverRunner.setWebDriver(webDriverBuilder.getAndroidDriver());
    } else if (platform.equals(Platform.IOS)) {
      WebDriverRunner.setWebDriver(webDriverBuilder.getIOsDriver());
    } else if (platform.equals(Platform.WEB) && cloudProvider.equals("browserstack")) {
      WebDriverRunner.setWebDriver(webDriverBuilder.getWebDriver());
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
  public String getPlatform() {
    return platform;
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
}
