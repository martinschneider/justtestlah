package io.github.martinschneider.justtestlah.base;

import com.applitools.eyes.selenium.Eyes;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.galenframework.api.Galen;
import com.galenframework.reports.GalenTestInfo;
import com.galenframework.reports.model.LayoutReport;
import io.appium.java_client.HasSettings;
import io.appium.java_client.Setting;
import io.github.martinschneider.justtestlah.configuration.JustTestLahConfiguration;
import io.github.martinschneider.justtestlah.locator.LocatorMap;
import io.github.martinschneider.justtestlah.locator.LocatorParser;
import io.github.martinschneider.justtestlah.visual.AppiumTemplateMatcher;
import io.github.martinschneider.justtestlah.visual.ImageUtils;
import io.github.martinschneider.justtestlah.visual.Match;
import io.github.martinschneider.justtestlah.visual.TemplateMatcher;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/** Base class for page objects. */
public abstract class BasePage<T> extends Base {
  protected static final Logger LOG = LoggerFactory.getLogger(BasePage.class);
  private static final double DEFAULT_MATCHING_THRESHOLD = 0.9; // for visual template matching
  protected JustTestLahConfiguration configuration;
  private LocatorMap locators;

  protected LocatorMap getLocators() {
    return locators;
  }

  @Autowired private LocatorParser locatorParser;

  @Autowired private TemplateMatcher templateMatcher;

  @Autowired private Eyes eyes;

  @Autowired private List<GalenTestInfo> galenTests;

  @Autowired private ImageUtils imageUtils;

  /**
   * Selenide style locator.
   *
   * @param locatorKey locator key (can include placeholders)
   * @param params parameters to replace the placeholders
   * @return {@link SelenideElement}
   */
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
    return findImage(imageName, threshold).isFound();
  }

  /**
   * Finds the given image within the current screen.
   *
   * @param imageName image to check for
   * @param threshold matching threshold
   * @return {@link Match}
   */
  public Match findImage(String imageName, double threshold) {
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
      return templateMatcher.match(
          screenshotFile.getAbsolutePath(), imageUtils.getFullPath(imageName), threshold);
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
      String baseFolder = parent.getPackage().getName().replaceAll("\\.", "/");
      fileName = baseFolder + "/" + baseName + ".yaml";
      parent = parent.getSuperclass();
    } while (!parent.equals(BasePage.class));
    loadLocators(fileName);
  }

  private void loadLocators(String fileName) {
    LOG.info("Loading locators from {}...", fileName);
    locators = new LocatorMap(locatorParser.parse(fileName));
  }

  /**
   * Performs visual checks using Applitools.
   *
   * @return this
   */
  @SuppressWarnings("unchecked")
  public T checkWindow() {
    if (configuration.isEyesEnabled()) {
      LOG.info("Eyes enabled, performing check on class {}", this.getClass().getSimpleName());
      eyes.checkWindow();
    } else {
      LOG.info(
          "Eyes disabled, skipping check on class {}. You can enable visual testing with "
              + "Applitools Eyes by setting eyes.enabled = true in justtestlah.properties.",
          this.getClass().getSimpleName());
    }
    return (T) this;
  }

  /**
   * Performs layout checks using Galen.
   *
   * @return this
   */
  @SuppressWarnings("unchecked")
  public T checkLayout() {
    if (configuration.isGalenEnabled()) {
      String baseName = this.getClass().getSimpleName();
      String baseFolder = this.getClass().getPackage().getName().replaceAll("\\.", "/");
      String specPath = baseFolder + "/" + configuration.getPlatform() + "/" + baseName + ".spec";
      LOG.info("Checking layout {}", specPath);
      String title = "Check layout " + specPath;
      LayoutReport layoutReport;
      try {
        layoutReport =
            Galen.checkLayout(
                WebDriverRunner.getWebDriver(),
                this.getClass().getClassLoader().getResource(specPath).getPath(),
                Collections.singletonList(configuration.getPlatform()));
        GalenTestInfo test = GalenTestInfo.fromString(this.getClass().getSimpleName());
        test.getReport().layout(layoutReport, title);
        galenTests.add(test);
      } catch (IOException e) {
        LOG.warn("Error checking layout", e);
      }
    } else {
      LOG.info(
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
}
