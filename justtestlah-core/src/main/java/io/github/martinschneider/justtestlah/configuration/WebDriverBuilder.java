package io.github.martinschneider.justtestlah.configuration;

import org.openqa.selenium.WebDriver;

public interface WebDriverBuilder {

  /**
   * Get an Android driver instance.
   *
   * @return {@link WebDriver} for Android
   */
  WebDriver getAndroidDriver();

  /**
   * Get an iOS driver instance.
   *
   * @return {@link WebDriver} for iOS
   */
  WebDriver getIOsDriver();

  /**
   * Get an iOS driver instance.
   *
   * @return {@link WebDriver} for Web
   */
  WebDriver getWebDriver();
}
