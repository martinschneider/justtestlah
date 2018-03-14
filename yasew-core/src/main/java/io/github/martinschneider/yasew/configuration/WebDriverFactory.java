package io.github.martinschneider.yasew.configuration;

import static io.appium.java_client.remote.AndroidMobileCapabilityType.APP_ACTIVITY;
import static io.appium.java_client.remote.AndroidMobileCapabilityType.APP_PACKAGE;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Value;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSElement;

/**
 * Factory for {@link WebDriver}
 * 
 * @author Martin Schneider
 */
public class WebDriverFactory {

	@Value("${android.deviceName}")
	private String androidDeviceName;
	
	@Value("${ios.deviceName}")
	private String iosDeviceName;

	@Value("${android.appPath}")
	private String androidAppPath;

	@Value("${ios.appPath}")
	private String iosAppPath;

	@Value("${android.appPackage}")
	private String appPackage;

	@Value("${android.appActivity}")
	private String appActivity;

	@Value("${mobile.appiumUrl}")
	private String appiumUrl;

	/**
	 * @return {@link WebDriver} for Android
	 */
	public WebDriver getAndroidDriver() {
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("deviceName", androidDeviceName);
		capabilities.setCapability("app", androidAppPath);
		capabilities.setCapability(APP_PACKAGE, appPackage);
		capabilities.setCapability(APP_ACTIVITY, appActivity);
		capabilities.setCapability("platformName", "android");
		try {
			return new AppiumDriver<AndroidElement>(new URL(appiumUrl), capabilities);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public WebDriver getIOSDriver()
	{
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("deviceName", iosDeviceName);
		capabilities.setCapability("app", iosAppPath);
		capabilities.setCapability("platformName", "iOS");
		capabilities.setCapability("automationName", "XCUITest");
		capabilities.setCapability("showXcodeLog", true);
		capabilities.setCapability("startIWDP", true);
		try {
			return new AppiumDriver<IOSElement>(new URL(appiumUrl), capabilities);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}
