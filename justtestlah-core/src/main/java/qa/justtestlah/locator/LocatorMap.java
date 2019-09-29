package qa.justtestlah.locator;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileBy.ByAccessibilityId;
import io.appium.java_client.MobileBy.ByAndroidUIAutomator;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qa.justtestlah.configuration.Platform;
import qa.justtestlah.visual.ImageUtils;

/** Map to hold element locators. */
public class LocatorMap {

  private static final Logger LOG = LoggerFactory.getLogger(LocatorMap.class);

  private Map<String, Map<String, Map<String, String>>> map;

  /** Default constructor. */
  public LocatorMap() {
    this.map = new HashMap<>();
  }

  /**
   * Construct a locator map from an existing {@link Map} object.
   *
   * @param map locator map
   */
  public LocatorMap(Map<String, Map<String, Map<String, String>>> map) {
    this.map = map;
  }

  private static final String CSS = "css";
  private static final String XPATH = "xpath";
  private static final String ID = "id";
  private static final String ACCESIBILITY_ID = "accesibilityId";
  private static final String UIAUTOMATOR = "uiAutomator";
  private static final String IMAGE = "image";

  /**
   * Get a Selenide locator.
   *
   * @param key locator key
   * @param platform platform
   * @param params locator key parameters
   * @return {@link SelenideElement}
   */
  public SelenideElement getLocator(String key, Platform platform, Object... params) {
    Pair<String, String> platformKey = getRawLocator(key, platform, params);
    String type = platformKey.getLeft();
    String rawValue = platformKey.getRight();
    LOG.debug("Getting locator {} of type {}", rawValue, type);

    if (type.equalsIgnoreCase(CSS)) {
      return $(By.cssSelector(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(XPATH)) {
      return $(By.xpath(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(ID)) {
      return $(By.id(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(ACCESIBILITY_ID)) {
      return $(ByAccessibilityId.AccessibilityId(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(UIAUTOMATOR)) {
      return $(ByAndroidUIAutomator.AndroidUIAutomator(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(IMAGE)) {
      return $(MobileBy.image(new ImageUtils().getImageAsBase64String(rawValue)));
    } else {
      return $(formatValue(rawValue, params));
    }
  }

  /**
   * Get a Selenide collection locator.
   *
   * @param key locator key
   * @param platform platform
   * @param params locator key parameters
   * @return {@link ElementsCollection}
   */
  public ElementsCollection getCollectionLocator(String key, Platform platform, Object... params) {
    Pair<String, String> platformKey = getRawLocator(key, platform, params);
    String type = platformKey.getLeft();
    String rawValue = platformKey.getRight();

    if (type.equalsIgnoreCase(CSS)) {
      return $$(By.cssSelector(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(XPATH)) {
      return $$(By.xpath(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(ID)) {
      return $$(By.id(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(ACCESIBILITY_ID)) {
      return $$(ByAccessibilityId.AccessibilityId(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(UIAUTOMATOR)) {
      return $$(ByAndroidUIAutomator.AndroidUIAutomator(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(IMAGE)) {
      return $$(MobileBy.image(new ImageUtils().getImageAsBase64String(rawValue)));
    } else {
      return $$(formatValue(rawValue, params));
    }
  }

  /**
   * Get the raw locator (type and value). This is exposed to be used for logging purposes.
   *
   * @param key locator key
   * @param platform platform
   * @param params locator key parameters
   * @return {@link Pair}
   */
  public Pair<String, String> getRawLocator(String key, Platform platform, Object... params) {
    Map<String, String> platformKey = map.get(key).get(platform.getPlatformName());
    return Pair.of(platformKey.get("type"), platformKey.get("value"));
  }

  private String formatValue(String rawValue, Object... params) {
    return String.format(rawValue, params);
  }
}
