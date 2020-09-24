package qa.justtestlah.base;

import static com.codeborne.selenide.Condition.appear;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.ElementNotFound;
import io.appium.java_client.HasSettings;
import io.appium.java_client.Setting;
import java.io.File;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import qa.justtestlah.annotations.ScreenIdentifier;
import qa.justtestlah.configuration.JustTestLahConfiguration;
import qa.justtestlah.exception.ScreenVerificationException;
import qa.justtestlah.locator.LocatorMap;
import qa.justtestlah.locator.LocatorParser;
import qa.justtestlah.locator.LocatorPlaceholders;
import qa.justtestlah.log.LogLevel;
import qa.justtestlah.log.TestLogWriter;
import qa.justtestlah.stubs.AppiumTemplateMatcher;
import qa.justtestlah.stubs.Galen;
import qa.justtestlah.stubs.Match;
import qa.justtestlah.stubs.OCR;
import qa.justtestlah.stubs.TemplateMatcher;
import qa.justtestlah.utils.ImageUtils;

/** Base class for page objects. */
public abstract class BasePage<T> extends Base {
  protected static final Logger LOG = LoggerFactory.getLogger(BasePage.class);
  protected static final Logger TESTLOG =
      LoggerFactory.getLogger(TestLogWriter.TESTLOG_LOGGER_NAME);
  protected static final int DEFAULT_VERIFICATION_TIMEOUT = 2000; // milliseconds
  protected JustTestLahConfiguration configuration;
  private LocatorMap locators;

  @Autowired private LocatorParser locatorParser;

  @Autowired private TemplateMatcher templateMatcher;

  @Autowired private LocatorPlaceholders globalPlaceholders;

  @Autowired private Galen galen;

  @Autowired private OCR ocr;

  @Autowired private TestLogWriter logWriter;

  protected LocatorMap getLocators() {
    return locators;
  }

  /**
   * Selenide style locator.
   *
   * @param locatorKey locator key (can include placeholders)
   * @param params parameters to replace the placeholders
   * @return {@link SelenideElement}
   */
  @SuppressWarnings("squid:S00100")
  protected SelenideElement $(String locatorKey, Object... params) {
    return Selenide.$(locators.getLocator(locatorKey, configuration.getPlatform(), params));
  }

  /**
   * Selenide style collection locator.
   *
   * @param locatorKey locator key (can include placeholders)
   * @param params parameters to replace the placeholders
   * @return {@link ElementsCollection}
   */
  @SuppressWarnings("squid:S00100")
  protected ElementsCollection $$(String locatorKey, Object... params) {
    return Selenide.$$(
        locators.getCollectionLocator(locatorKey, configuration.getPlatform(), params));
  }

  /**
   * @param imageName image to check for
   * @return true, if the image has been found on the current screen
   */
  public boolean hasImage(String imageName) {
    return hasImage(imageName, 0.9);
  }

  /**
   * Checks for the given image within the current screen.
   *
   * @param imageName image to check for
   * @param threshold matching threshold
   * @return true, if the image has been found on the current screen
   */
  public boolean hasImage(String imageName, double threshold) {
    return findImage(imageName, threshold).isDisplayed();
  }

  /**
   * Finds the given image within the current screen using the default threshold.
   *
   * @param imageName image to check for
   * @return {@link WebElement}
   */
  public WebElement findImage(String imageName) {
    return findImage(imageName, 0.9);
  }

  /**
   * Finds the given image within the current screen.
   *
   * @param imageName image to check for
   * @param threshold matching threshold
   * @return {@link WebElement}
   */
  public WebElement findImage(String imageName, double threshold) {
    WebDriver driver = WebDriverRunner.getWebDriver();
    if (driver instanceof TakesScreenshot) {
      File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
      if (templateMatcher instanceof AppiumTemplateMatcher) {
        if (driver instanceof HasSettings) {
          LOG.info("Setting image matching threshold to {}", threshold);
          HasSettings settingsDriver = ((HasSettings) driver);
          settingsDriver.setSetting(Setting.IMAGE_MATCH_THRESHOLD, threshold);
        }
        ((AppiumTemplateMatcher) templateMatcher).setDriver(WebDriverRunner.getWebDriver());
      }
      String path = ImageUtils.getFullPath(imageName);
      return new ImageWebElement(
          driver,
          templateMatcher.match(screenshotFile.getAbsolutePath(), path, threshold).getRect(),
          ocr,
          path);
    } else {
      throw new UnsupportedOperationException(
          "This operation is not supported for the current WebDriver: "
              + driver.getClass().getSimpleName()
              + ".");
    }
  }

  /** Initialize the {@link LocatorMap}. */
  @PostConstruct
  public void initializeLocatorMap() {
    Class<?> parent = this.getClass();
    String fileName = null;
    do {
      String baseName = parent.getSimpleName();
      String baseFolder = parent.getPackage().getName().replace(".", File.separator);
      fileName = baseFolder + File.separator + baseName + ".yaml";
      parent = parent.getSuperclass();
    } while (!parent.equals(BasePage.class));
    loadLocators(fileName);
  }

  private void loadLocators(String fileName) {
    LOG.info("Loading locators from {}", fileName);
    locators = new LocatorMap(locatorParser.parse(fileName), globalPlaceholders.getProps());
  }

  /**
   * Performs layout checks using Galen.
   *
   * @return this
   */
  @SuppressWarnings("unchecked")
  private T checkLayout() {
    if (configuration.isGalenEnabled()) {
      String baseName = this.getClass().getSimpleName();
      String baseFolder = this.getClass().getPackage().getName().replaceAll("\\.", File.separator);
      String specPath =
          baseFolder
              + File.separator
              + configuration.getPlatform().toString().toLowerCase()
              + File.separator
              + baseName
              + ".spec";
      galen.checkLayout(specPath, locators);
    } else {
      LOG.debug(
          "Galen checks disabled, skipping checks for class {}. "
              + "You can enable Galen by setting galen.enabled = true in justtestlah.properties.",
          this.getClass().getSimpleName());
    }
    return (T) this;
  }

  @Autowired
  public void setConfiguration(JustTestLahConfiguration configuration) {
    this.configuration = configuration;
  }

  /** @return this page object */
  public T verify() {
    return verify(DEFAULT_VERIFICATION_TIMEOUT);
  }

  /**
   * Verifies, that all UI elements defined for the given page object using {@link ScreenIdentifier}
   * are displayed.
   *
   * <p>Performs Galen checks, if enabled.
   *
   * @param timeout the timeout for identifying the first element. Note, that there is no timeout
   *     for any subsequent checks!
   * @return this page object
   */
  @SuppressWarnings("unchecked")
  public T verify(int timeout) {
    boolean initialCheck = true;
    // Galen
    checkLayout();
    Class<?> clazz = this.getClass();
    logWriter.log(
        LogLevel.INFO,
        TestLogWriter.WEBDRIVER_INDENTATION,
        "Verifying screen identifiers for {}",
        clazz.getSimpleName());
    while (clazz != Base.class) {
      for (ScreenIdentifier identifiers : clazz.getAnnotationsByType(ScreenIdentifier.class)) {
        for (String identifier : identifiers.value()) {
          // rawLocator is only used for logging purposes
          Pair<String, String> rawLocator =
              locators.getRawLocator(identifier, configuration.getPlatform());
          try {
            // only use the timeout for the first check
            if (initialCheck) {
              $(identifier).waitUntil(appear, timeout).isDisplayed();
            } else {
              $(identifier).waitUntil(appear, 0).isDisplayed();
            }
            initialCheck = false;
          } catch (ElementNotFound exception) {
            throw new ScreenVerificationException(
                identifier, rawLocator, this.getClass().getSimpleName(), timeout);
          }
          logWriter.log(
              LogLevel.INFO,
              TestLogWriter.WEBDRIVER_INDENTATION,
              "[OK] {} is displayed {}:{}",
              identifier,
              rawLocator.getLeft(),
              rawLocator.getRight());
        }
      }
      clazz = clazz.getSuperclass();
    }
    return (T) this;
  }
}
