package io.github.martinschneider.yasew.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.Before;
import io.github.martinschneider.yasew.configuration.YasewConfiguration;

/**
 * Hook to restart the WebDriver before every test
 * 
 * @author Martin Schneider
 */
public class CucumberHooks {

	private Logger LOG = LoggerFactory.getLogger(CucumberHooks.class);

	@Autowired
	private YasewConfiguration configuration;

	@Before
	public void restartDriver() {
		LOG.info("Initializing web driver");
		configuration.initWebDriver();
	}
}
