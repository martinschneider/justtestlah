package qa.justtestlah.steps;

import com.codeborne.selenide.WebDriverRunner;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import qa.justtestlah.configuration.JustTestLahConfiguration;
import qa.justtestlah.configuration.SpringConfig;

/** Hook to restart the WebDriver before every test. */
@ContextConfiguration(classes = SpringConfig.class)
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
   * Close the web driver.
   *
   * @throws IOException {@link IOException}
   */
  @After(order = 1)
  public void shutdown() throws IOException {
    LOG.info("Closing web driver");
    WebDriverRunner.closeWebDriver();
  }
}
