package io.github.martinschneider.yasew.configuration;

import org.openqa.selenium.WebDriver;

public interface WebDriverBuilder {

	/**
	 * @return {@link WebDriver} for Android
	 */
	WebDriver getAndroidDriver();

	WebDriver getIOSDriver();

}