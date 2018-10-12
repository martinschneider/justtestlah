package io.github.martinschneider.yasew.locator;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.appium.java_client.MobileBy.ByAccessibilityId;
import io.appium.java_client.MobileBy.ByAndroidUIAutomator;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.By;

/** Map to hold element locators. */
public class LocatorMap {

  private Map<String, Map<String, Map<String, String>>> map;

  /**
   * Default constructor.
   */
  public LocatorMap() {
    this.map = new HashMap<String, Map<String, Map<String, String>>>();
  }  
  
  /**
   * @param map locator map
   */
  public LocatorMap(Map<String, Map<String, Map<String, String>>> map) {
    this.map = map;
  }

  /**
   * @return the locator map
   */
  public Map<String, Map<String, Map<String, String>>> getMap() {
    return map;
  }

  private static final String CSS = "css";
  private static final String XPATH = "xpath";
  private static final String ID = "id";
  private static final String ACCESIBILITY_ID = "accesibilityId";
  private static final String UIAUTOMATOR = "uiAutomator";

  /**
   * Get a Selenide locator.
   * 
   * @param key locator key
   * @param platform platform
   * @param params locator key parameters
   * @return {@link SelenideElement}
   */
  public SelenideElement getLocator(String key, String platform, Object... params) {
    Map<String, String> platformKey = map.get(key).get(platform);
    String type = platformKey.get("type");
    String rawValue = platformKey.get("value");

    if (type.equalsIgnoreCase(CSS)) {
      return $(By.cssSelector(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(XPATH)) {
      return $(By.xpath(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(ID)) {
      return $(By.id(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(ACCESIBILITY_ID)) {
      return $(ByAccessibilityId.AccessibilityId(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(UIAUTOMATOR)) {
      return $(ByAndroidUIAutomator.AccessibilityId(formatValue(rawValue, params)));
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
  public ElementsCollection getCollectionLocator(String key, String platform, Object... params) {
    Map<String, String> platformKey = map.get(key).get(platform);
    String type = platformKey.get("type");
    String rawValue = platformKey.get("value");

    if (type.equalsIgnoreCase(CSS)) {
      return $$(By.cssSelector(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(XPATH)) {
      return $$(By.xpath(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(ID)) {
      return $$(By.id(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(ACCESIBILITY_ID)) {
      return $$(ByAccessibilityId.AccessibilityId(formatValue(rawValue, params)));
    } else if (type.equalsIgnoreCase(UIAUTOMATOR)) {
      return $$(ByAndroidUIAutomator.AccessibilityId(formatValue(rawValue, params)));
    } else {
      return $$(formatValue(rawValue, params));
    }
  }

  private String formatValue(String rawValue, Object... params) {
    return String.format(rawValue, params);
  }
}
