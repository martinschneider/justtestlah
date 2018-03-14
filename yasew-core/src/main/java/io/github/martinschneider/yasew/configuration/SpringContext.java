package io.github.martinschneider.yasew.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

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

	@Bean
	public YasewConfiguration config() {
		return new YasewConfiguration(webDriverFactory(), userService());
	}

	@Bean
	public WebDriverFactory webDriverFactory() {
		return new WebDriverFactory();
	}

	@Bean
	public TemplateMatcher templateMatcher() {
		return new TemplateMatcher();
	}

	@Bean
	public UserService userService() {
		return new UserService();
	}
}
