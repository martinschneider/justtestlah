package io.github.martinschneider.yasew.base;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;

import io.github.martinschneider.yasew.configuration.YasewConfiguration;
import io.github.martinschneider.yasew.locator.LocatorMap;
import io.github.martinschneider.yasew.visual.TemplateMatcher;

/**
 * Base class for page objects
 * 
 * @author Martin Schneider
 */
public abstract class BasePage extends Base {
	private final static Logger LOG = LoggerFactory.getLogger(BasePage.class);
	private final static String IMAGE_FOLDER = "images";
	private static final double MATCHING_THRESHOLD = 0.9; // for visual template matching
	protected YasewConfiguration configuration;
	private LocatorMap locators = new LocatorMap();

	@Autowired
	private TemplateMatcher templateMatcher;

	/**
	 * @param locatorKey
	 *            locator key (can include placeholders)
	 * @param params
	 *            parameters to replace the placeholders
	 * @return {@link SelenideElement}
	 */
	protected SelenideElement $(String locatorKey, Object... params) {
		return Selenide.$(locators.getLocator(locatorKey, params));
	}
	
	/**
	 * @param locatorKey
	 *            locator key (can include placeholders)
	 * @param params
	 *            parameters to replace the placeholders
	 * @return {@link ElementsCollection}
	 */
	protected ElementsCollection $$(String locatorKey, Object... params) {
		return Selenide.$$(locators.getCollectionLocator(locatorKey, params));
	}

	public boolean hasImage(String imageName) {
		return hasImage(imageName, Configuration.timeout);
	}

	public boolean hasImage(String imageName, long timeout) {
		WebDriver driver = WebDriverRunner.getWebDriver();
		if (driver instanceof TakesScreenshot) {
			File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			return templateMatcher.match(screenshotFile.getAbsolutePath(),
					this.getClass().getClassLoader().getResource(IMAGE_FOLDER + "/" + imageName).getFile(),
					MATCHING_THRESHOLD);
		} else {
			throw new UnsupportedOperationException("This operation is not supported for the current WebDriver: "
					+ driver.getClass().getSimpleName() + ".");
		}
	}

	/**
	 * initialize the {@link LocatorMap}
	 */
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

	@Autowired
	public void setConfiguration(YasewConfiguration configuration) {
		this.configuration = configuration;
	}
}
