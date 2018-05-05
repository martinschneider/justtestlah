package io.github.martinschneider.yasew.locator;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import java.util.HashMap;
import org.openqa.selenium.By;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.appium.java_client.MobileBy.ByAccessibilityId;
import io.appium.java_client.MobileBy.ByAndroidUIAutomator;

/** Map to hold element locators */
public class LocatorMap extends HashMap<String, String> {

  private static final long serialVersionUID = 1L;
  private static final String CSS = "CSS";
  private static final String XPATH = "XPATH";
  private static final String ID = "ID";
  private static final String ACCESIBILITY_ID = "ACCESIBILITY_ID";
  private static final String UIAUTOMATOR = "UIAUTOMATOR";
  private static final String SEPARATOR = "|";

  public SelenideElement getLocator(Object key, Object... params) {
    String rawValue = get(key);
    if (rawValue.startsWith(CSS + SEPARATOR)) {
      return $(By.cssSelector(formatValue(cutPrefix(rawValue, CSS), params)));
    } else if (rawValue.startsWith(XPATH + SEPARATOR)) {
      return $(By.xpath(formatValue(cutPrefix(rawValue, XPATH), params)));
    } else if (rawValue.startsWith(ID + SEPARATOR)) {
      return $(By.id(formatValue(cutPrefix(rawValue, ID), params)));
    } else if (rawValue.startsWith(ACCESIBILITY_ID + SEPARATOR)) {
      return $(
          ByAccessibilityId.AccessibilityId(
              formatValue(cutPrefix(rawValue, ACCESIBILITY_ID), params)));
    } else if (rawValue.startsWith(UIAUTOMATOR)) {
      return $(
          ByAndroidUIAutomator.AccessibilityId(
              formatValue(cutPrefix(rawValue, UIAUTOMATOR), params)));
    } else {
      return $(formatValue(rawValue, params));
    }
  }

  public ElementsCollection getCollectionLocator(Object key, Object... params) {
    String rawValue = get(key);
    if (rawValue.startsWith(CSS + SEPARATOR)) {
      return $$(By.cssSelector(formatValue(cutPrefix(rawValue, CSS), params)));
    } else if (rawValue.startsWith(XPATH + SEPARATOR)) {
      return $$(By.xpath(formatValue(cutPrefix(rawValue, XPATH), params)));
    } else if (rawValue.startsWith(ID + SEPARATOR)) {
      return $$(By.id(formatValue(cutPrefix(rawValue, ID), params)));
    } else if (rawValue.startsWith(ACCESIBILITY_ID + SEPARATOR)) {
      return $$(
          ByAccessibilityId.AccessibilityId(
              formatValue(cutPrefix(rawValue, ACCESIBILITY_ID), params)));
    } else if (rawValue.startsWith(UIAUTOMATOR)) {
      return $$(
          ByAndroidUIAutomator.AccessibilityId(
              formatValue(cutPrefix(rawValue, UIAUTOMATOR), params)));
    } else {
      return $$(formatValue(rawValue, params));
    }
  }

  private String formatValue(String rawValue, Object... params) {
    return String.format(rawValue, params);
  }

  private String cutPrefix(String rawValue, String prefix) {
    return rawValue.substring(prefix.length() + SEPARATOR.length());
  }
}
