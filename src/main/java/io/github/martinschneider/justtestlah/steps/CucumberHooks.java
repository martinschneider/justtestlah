package io.github.martinschneider.justtestlah.steps;

import com.applitools.eyes.selenium.Eyes;
import com.codeborne.selenide.WebDriverRunner;
import com.galenframework.reports.GalenTestInfo;
import com.galenframework.reports.HtmlReportBuilder;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import io.github.martinschneider.justtestlah.configuration.SpringContext;
import io.github.martinschneider.justtestlah.configuration.JustTestLahConfiguration;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/** Hook to restart the WebDriver before every test. */
@ContextConfiguration(classes = SpringContext.class)
public class CucumberHooks {

  private static final String GALEN_REPORT_FOLDER_DATE_PATTERN = "yyyy-MM-dd HH.mm.ss";

  private static final Logger LOG = LoggerFactory.getLogger(CucumberHooks.class);

  @Autowired private JustTestLahConfiguration configuration;

  @Autowired private Eyes eyes;

  @Autowired private List<GalenTestInfo> galenTests;

  /** Initialise the web driver and (optionally) Applitools. */
  @Before
  public void init() {
    LOG.info("Initializing web driver");
    configuration.initWebDriver();
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
  public void shutdown() throws IOException {
    WebDriverRunner.closeWebDriver();
    if (configuration.isEyesEnabled() && eyes.getIsOpen()) {
      LOG.info("Closing Eyes");
      eyes.close();
    }
    if (configuration.isGalenEnabled()) {
      LOG.info("Generating {} Galen reports", galenTests.size());
      new HtmlReportBuilder().build(galenTests, getGalenReportDirectory());
    }
  }

  private String getGalenReportDirectory() {
    return configuration.getGalenReportDirectory()
        + "/"
        + LocalDateTime.now().format(DateTimeFormatter.ofPattern(GALEN_REPORT_FOLDER_DATE_PATTERN));
  }
}
