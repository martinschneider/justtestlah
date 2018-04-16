package io.github.martinschneider.yasew.configuration;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.applitools.eyes.selenium.Eyes;
import com.galenframework.reports.GalenTestInfo;

import io.github.martinschneider.yasew.user.UserService;
import io.github.martinschneider.yasew.visual.TemplateMatcher;

/**
 * YASeW Spring context
 * 
 * @author Martin Schneider
 */
@Configuration
@PropertySource(value = { "yasew.properties", "file:${yasew.properties}" }, ignoreResourceNotFound = true)
@ComponentScan(basePackages = "${pages.package}")
public class SpringContext {

	@Value("${eyes.apiKey}")
	private String eyesApiKey;

	@Value("${cloudprovider:local}")
	private String cloudProvider;

	private List<GalenTestInfo> galenTests = new LinkedList<GalenTestInfo>();

	@Bean
	public YasewConfiguration config() {
		return new YasewConfiguration(webDriverFactory(), userService());
	}

	@Bean
	public WebDriverBuilder webDriverFactory() {
		if (cloudProvider.equals("browserstack")) {
			return new BrowserStackWebDriverBuilder();
		}
		return new LocalWebDriverBuilder();
	}

	@Bean
	public TemplateMatcher templateMatcher() {
		return new TemplateMatcher();
	}

	@Bean
	public UserService userService() {
		return new UserService();
	}

	@Bean
	public Eyes eyes() {
		Eyes eyes = new Eyes();
		eyes.setApiKey(eyesApiKey);
		return eyes;
	}

	@Bean
	public List<GalenTestInfo> galenTests() {
		return galenTests;
	}
}
