package io.github.martinschneider.justtestlah.steps;

import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import io.github.martinschneider.justtestlah.configuration.JustTestLahConfiguration;
import io.github.martinschneider.justtestlah.configuration.SpringContext;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/** Hook to restart the WebDriver before every test. */
@ContextConfiguration(classes = SpringContext.class)
public class WebDriverHooks {

  private static final Logger LOG = LoggerFactory.getLogger(WebDriverHooks.class);

  @Autowired private JustTestLahConfiguration configuration;

  /** Initialise the web driver. */
  @Before(order = 1) // this needs to run before initialising Applitools
  public void init() {
    LOG.info("Initializing web driver");
    configuration.initWebDriver();
  }

  /**
   * Close the web driver and Applitools. Generate Galen reports.
   *
   * @throws IOException {@link IOException}
   */
  @After(order = 1)
  public void shutdown() throws IOException {
    WebDriverRunner.closeWebDriver();
  }
}
