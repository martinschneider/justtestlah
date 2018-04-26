package io.github.martinschneider.yasew.base;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.annotation.PostConstruct;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.applitools.eyes.selenium.Eyes;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.galenframework.api.Galen;
import com.galenframework.reports.GalenTestInfo;
import com.galenframework.reports.model.LayoutReport;
import io.github.martinschneider.yasew.configuration.YasewConfiguration;
import io.github.martinschneider.yasew.locator.LocatorMap;
import io.github.martinschneider.yasew.visual.TemplateMatcher;

/** Base class for page objects */
public abstract class BasePage<T> extends Base {
  private static final Logger LOG = LoggerFactory.getLogger(BasePage.class);
  private static final String IMAGE_FOLDER = "images";
  private static final double MATCHING_THRESHOLD = 0.9; // for visual template matching
  protected YasewConfiguration configuration;
  private LocatorMap locators = new LocatorMap();

  protected LocatorMap getLocators() {
    return locators;
  }

  @Autowired private TemplateMatcher templateMatcher;

  @Autowired private Eyes eyes;

  @Autowired private List<GalenTestInfo> galenTests;

  /**
   * @param locatorKey locator key (can include placeholders)
   * @param params parameters to replace the placeholders
   * @return {@link SelenideElement}
   */
  @SuppressWarnings("squid:S00100Method") // the method name is on purpose
  protected SelenideElement $(String locatorKey, Object... params) {
    return Selenide.$(locators.getLocator(locatorKey, params));
  }

  /**
   * @param locatorKey locator key (can include placeholders)
   * @param params parameters to replace the placeholders
   * @return {@link ElementsCollection}
   */
  @SuppressWarnings("squid:S00100Method") // the method name is on purpose
  protected ElementsCollection $$(String locatorKey, Object... params) {
    return Selenide.$$(locators.getCollectionLocator(locatorKey, params));
  }

  public boolean hasImage(String imageName) {
    return hasImage(imageName, MATCHING_THRESHOLD);
  }

  public boolean hasImage(String imageName, double threshold) {
    WebDriver driver = WebDriverRunner.getWebDriver();
    if (driver instanceof TakesScreenshot) {
      File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
      return templateMatcher.match(
          screenshotFile.getAbsolutePath(),
          this.getClass().getClassLoader().getResource(IMAGE_FOLDER + "/" + imageName).getFile(),
          threshold);
    } else {
      throw new UnsupportedOperationException(
          "This operation is not supported for the current WebDriver: "
              + driver.getClass().getSimpleName()
              + ".");
    }
  }

  /** initialize the {@link LocatorMap} */
  @PostConstruct
  public void initializeLocatorMap() {
    Class<?> parent = this.getClass();
    do {
      String baseName = parent.getSimpleName();
      String baseFolder = parent.getPackage().getName().replaceAll("\\.", "/");
      // load general locators
      loadLocators(baseFolder + "/" + baseName + ".properties");
      // load platform-specific locators
      loadLocators(baseFolder + "/" + configuration.getPlatform() + "/" + baseName + ".properties");
      parent = parent.getSuperclass();
    } while (!parent.equals(BasePage.class));
  }

  private void loadLocators(String fileName) {
    LOG.info("Loading message properties from {}...", fileName);
    Properties props = new Properties();
    try {
      props.load(BasePage.class.getClassLoader().getResourceAsStream(fileName));
    } catch (NullPointerException | IOException e) {
      LOG.warn("Error loading message properties from {}", fileName);
    }
    for (final String name : props.stringPropertyNames()) {
      locators.put(name, props.getProperty(name));
    }
  }

  /**
   * Performs visual checks using Applitools
   *
   * @return this
   */
  public T checkWindow() {
    if (configuration.isEyesEnabled()) {
      LOG.info("Eyes enabled, performing check on class {}", this.getClass().getSimpleName());
      eyes.checkWindow();
    } else {
      LOG.info(
          "Eyes disabled, skipping check on class {}. You can enable visual testing with Applitools Eyes by setting eyes.enabled = true in yasew.properties.",
          this.getClass().getSimpleName());
    }
    return (T) this;
  }

  /**
   * Performs layout checks using Galen
   *
   * @return this
   */
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
                Collections.singletonList(configuration.getPlatform()),
                Collections.<String>emptyList(),
                new Properties(),
                null);
        GalenTestInfo test = GalenTestInfo.fromString(this.getClass().getSimpleName());
        test.getReport().layout(layoutReport, title);
        galenTests.add(test);
      } catch (IOException e) {
        LOG.warn("Error checking layout", e);
      }
    } else {
      LOG.info(
          "Galen checks disabled, skipping checks for class {}. You can enable Galen by setting galen.enabled = true in yasew.properties.",
          this.getClass().getSimpleName());
    }
    return (T) this;
  }

  @Autowired
  public void setConfiguration(YasewConfiguration configuration) {
    this.configuration = configuration;
  }
}
