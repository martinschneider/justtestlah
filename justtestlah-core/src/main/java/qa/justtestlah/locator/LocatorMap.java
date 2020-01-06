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
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qa.justtestlah.configuration.Platform;
import qa.justtestlah.utils.ImageUtils;

/** Map to hold element locators. */
public class LocatorMap {

  private static final Logger LOG = LoggerFactory.getLogger(LocatorMap.class);

  private Map<String, Map<String, Map<String, String>>> map;

  private Properties staticPlaceholders;

  private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{(\\w*)\\}");

  private static final String CSS = "css";
  private static final String XPATH = "xpath";
  private static final String ID = "id";
  private static final String ACCESIBILITY_ID = "accesibilityId";
  private static final String UIAUTOMATOR = "uiAutomator";
  private static final String IMAGE = "image";

  /** Default constructor. */
  public LocatorMap() {
    this.map = new HashMap<>();
  }

  /**
   * Construct a locator map from an existing {@link Map} object.
   *
   * @param map locator map
   * @param staticPlaceholders static placeholders to be replaced in any locator
   */
  public LocatorMap(
      Map<String, Map<String, Map<String, String>>> map, Properties staticPlaceholders) {
    this.map = map;
    this.staticPlaceholders = staticPlaceholders;
  }

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
      return $(MobileBy.image(ImageUtils.getImageAsBase64String(rawValue)));
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
      return $$(MobileBy.image(ImageUtils.getImageAsBase64String(rawValue)));
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
    Map<String, String> tuple = map.get(key).get(platform.getPlatformName());
    return Pair.of(tuple.get("type"), tuple.get("value"));
  }

  /**
   * @param platform {@link Platform}
   * @return a map of all UI locators specified for the given platform
   */
  public Map<String, Pair<String, String>> getLocatorsForPlatform(Platform platform) {
    Map<String, Pair<String, String>> result = new HashMap<>();
    for (Map.Entry<String, Map<String, Map<String, String>>> entry : map.entrySet()) {
      Map<String, String> tuple = entry.getValue().get(platform.getPlatformName());
      if (tuple != null) {
        result.put(entry.getKey(), Pair.of(tuple.get("type"), tuple.get("value")));
      }
    }
    return result;
  }

  protected String formatValue(String rawValue, Object... params) {
    if (rawValue == null) {
      return null;
    }
    return String.format(replacePlaceholders(rawValue), params);
  }

  protected String replacePlaceholders(String rawValue) {
    if (rawValue == null) {
      return null;
    }
    Matcher matcher = PLACEHOLDER_PATTERN.matcher(rawValue);
    StringBuffer strBuffer = new StringBuffer();
    while (matcher.find()) {
      matcher.appendReplacement(strBuffer, staticPlaceholders.get(matcher.group(1)).toString());
    }
    matcher.appendTail(strBuffer);
    return strBuffer.toString();
  }
}
