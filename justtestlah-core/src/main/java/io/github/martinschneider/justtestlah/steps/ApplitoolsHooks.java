package io.github.martinschneider.justtestlah.steps;

import com.applitools.eyes.selenium.Eyes;
import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import io.github.martinschneider.justtestlah.configuration.JustTestLahConfiguration;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/** Hook to restart the WebDriver before every test. */
public class ApplitoolsHooks {

  private static final Logger LOG = LoggerFactory.getLogger(ApplitoolsHooks.class);

  @Autowired private JustTestLahConfiguration configuration;

  @Autowired private Eyes eyes;

  /** Initialise Applitools. */
  @Before
  public void init() {
    if (configuration.isEyesEnabled()) {
      LOG.info("Initializing Eyes");
      eyes.open(
          WebDriverRunner.getWebDriver(),
          configuration.getApplicationName(),
          configuration.getPlatform());
    }
  }

  /**
   * Close the web driver and Applitools. Generate Galen reports.
   *
   * @throws IOException {@link IOException}
   */
  @After
  public void close() throws IOException {
    if (configuration.isEyesEnabled() && eyes.getIsOpen()) {
      LOG.info("Closing Eyes");
      eyes.close();
    }
  }
}
