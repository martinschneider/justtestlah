package qa.justtestlah.hooks;

import com.codeborne.selenide.WebDriverRunner;
import io.cucumber.core.api.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qa.justtestlah.configuration.JustTestLahConfiguration;

/** Hook to restart the WebDriver before every test. */
@Component
public class WebDriverHooks extends AbstractCucumberHook {

  private static final Logger LOG = LoggerFactory.getLogger(WebDriverHooks.class);

  @Autowired private JustTestLahConfiguration configuration;

  /**
   * Initialise the web driver.
   *
   * @param scenario Cucumber scenario
   */
  public void before(Scenario scenario) {
    LOG.info("Initializing web driver");
    configuration.initWebDriver();
  }

  /**
   * Close the web driver.
   *
   * @param scenario Cucumber scenario
   */
  public void after(Scenario scenario) {
    LOG.info("Closing web driver");
    WebDriverRunner.closeWebDriver();
  }
}
