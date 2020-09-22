package qa.justtestlah.stubs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import qa.justtestlah.locator.LocatorMap;

/**
 * Placeholder implementations for beans from other modules
 *
 * <p>These stubs satisfy compile-time dependencies between justtestlah-core and its submodules.
 *
 * <p>If a sub-module is on the classpath, its bean definitions will take precedence over this
 * class. This is achieved by annotating the beans with {@link
 * org.springframework.context.annotation.Primary}).
 */
@Component
public class Stubs implements Applitools, Galen, TemplateMatcher, OCR {

  private Logger LOG = LoggerFactory.getLogger(Stubs.class);

  public void checkWindow() {
    throw new UnsupportedOperationException(
        "Applitools requires justtestlah-applitools.jar on the classpath.");
  }

  @Override
  public void checkLayout(String specPath, LocatorMap locators) {
    throw new UnsupportedOperationException(
        "Galen requires justtestlah-galen.jar on the classpath.");
  }

  @Override
  public Match match(String targetFile, String templateFile, double threshold) {
    throw new UnsupportedOperationException(
        "Template matching requires justtestlah-visual.jar on the classpath.");
  }

  @Override
  public Match match(String targetFile, String templateFile, double threshold, String description) {
    throw new UnsupportedOperationException(
        "Template matching requires justtestlah-visual.jar on the classpath.");
  }

  @Override
  public void setDriver(WebDriver driver) {
    LOG.warn("OCR requires justtestlah-visual.jar on the classpath.");
  }

  @Override
  public String getText(WebElement element) {
    throw new UnsupportedOperationException(
        "OCR requires justtestlah-visual.jar on the classpath.");
  }
}
