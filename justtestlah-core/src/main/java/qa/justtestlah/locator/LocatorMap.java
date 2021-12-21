package qa.justtestlah.locator;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.appium.java_client.AppiumBy;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qa.justtestlah.base.ByImage;
import qa.justtestlah.configuration.Platform;
import qa.justtestlah.stubs.OCR;
import qa.justtestlah.stubs.TemplateMatcher;
import qa.justtestlah.utils.ImageUtils;

/** Map to hold element locators. */
public class LocatorMap {

  private static final Logger LOG = LoggerFactory.getLogger(LocatorMap.class);

  private Map<String, Map<String, Map<String, Object>>> map;

  private Properties staticPlaceholders;

  private TemplateMatcher templateMatcher;

  private OCR ocr;

  private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{(\\w*)\\}");

  private static final String CSS = "css";
  private static final String XPATH = "xpath";
  private static final String ID = "id";
  private static final String ACCESIBILITY_ID = "accesibilityId";
  private static final String UIAUTOMATOR = "uiAutomator";
  private static final String IMAGE = "image"; // Appium
  private static final String OPENCV = "opencv";

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
      Map<String, Map<String, Map<String, Object>>> map,
      Properties staticPlaceholders,
      TemplateMatcher templateMatcher,
      OCR ocr) {
    this.map = map;
    this.staticPlaceholders = staticPlaceholders;
    this.templateMatcher = templateMatcher;
    this.ocr = ocr;
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
    Map<String, Object> platformKey = getRawLocator(key, platform, params);
    if (platformKey == null) {
      LOG.error("Locator with key {} is undefined for platform {}.", key, platform);
      return null;
    }
    String type = platformKey.get("type").toString();
    String rawValue = platformKey.get("value").toString();
    LOG.debug("Getting locator {} of type {}", rawValue, type);
    if (type.equalsIgnoreCase(CSS)) {
      return $(By.cssSelector(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(XPATH)) {
      return $(By.xpath(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(ID)) {
      return $(By.id(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(ACCESIBILITY_ID)) {
      return $(AppiumBy.accessibilityId(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(UIAUTOMATOR)) {
      return $(AppiumBy.androidUIAutomator(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(IMAGE)) {
      return $(AppiumBy.image(ImageUtils.getImageAsBase64String(rawValue)));
    } else if (type.equalsIgnoreCase(OPENCV)) {
      return $(
          ByImage.image(
              formatValue(rawValue, params), getThreshold(platformKey), templateMatcher, ocr));
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
    Map<String, Object> platformKey = getRawLocator(key, platform, params);
    String type = platformKey.get("type").toString();
    String rawValue = platformKey.get("value").toString();
    if (type.equalsIgnoreCase(CSS)) {
      return $$(By.cssSelector(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(XPATH)) {
      return $$(By.xpath(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(ID)) {
      return $$(By.id(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(ACCESIBILITY_ID)) {
      return $$(AppiumBy.accessibilityId(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(UIAUTOMATOR)) {
      return $$(AppiumBy.androidUIAutomator(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(IMAGE)) {
      return $$(AppiumBy.image(ImageUtils.getImageAsBase64String(rawValue)));
    } else if (type.equalsIgnoreCase(OPENCV)) {
      return $$(
          ByImage.image(
              formatValue(rawValue, params), getThreshold(platformKey), templateMatcher, ocr));
    } else {
      return $$(formatValue(rawValue, params));
    }
  }

  /**
   * Get the raw locator (type and value). This is exposed for logging purposes.
   *
   * @param key locator key
   * @param platform platform
   * @param params locator key parameters
   * @return {@link Pair}
   */
  public Map<String, Object> getRawLocator(String key, Platform platform, Object... params) {
    return map.get(key).get(platform.getPlatformName());
  }

  /**
   * @param platform {@link Platform}
   * @return a map of all UI locators specified for the given platform
   */
  public Map<String, Pair<String, String>> getLocatorsForPlatform(Platform platform) {
    Map<String, Pair<String, String>> result = new HashMap<>();
    for (Map.Entry<String, Map<String, Map<String, Object>>> entry : map.entrySet()) {
      Map<String, Object> tuple = entry.getValue().get(platform.getPlatformName());
      if (tuple != null) {
        result.put(
            entry.getKey(),
            Pair.of(
                tuple.get("type").toString(), replacePlaceholders(tuple.get("value").toString())));
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

  private double getThreshold(Map<String, ?> platformKey) {
    try {
      return (double) platformKey.get("threshold");
    } catch (Exception e) {
      LOG.warn(
          "Invalid or no threshold value {}. Using default: {}",
          platformKey.get("threshold"),
          ByImage.DEFAULT_THRESHOLD);
    }
    return ByImage.DEFAULT_THRESHOLD;
  }
}
