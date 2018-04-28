package io.github.martinschneider.yasew.steps;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.applitools.eyes.selenium.Eyes;
import com.codeborne.selenide.WebDriverRunner;
import com.galenframework.reports.GalenTestInfo;
import com.galenframework.reports.HtmlReportBuilder;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import io.github.martinschneider.yasew.configuration.YasewConfiguration;

/** Hook to restart the WebDriver before every test */
public class CucumberHooks {

  private Logger LOG = LoggerFactory.getLogger(CucumberHooks.class);

  @Autowired private YasewConfiguration configuration;

  @Autowired private Eyes eyes;

  @Autowired private List<GalenTestInfo> galenTests;

  @Before
  public void restartDriver() {
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

  @After
  public void shutdown() throws IOException {
    WebDriverRunner.closeWebDriver();
    if (configuration.isEyesEnabled() && eyes.getIsOpen()) {
      LOG.info("Closing Eyes");
      eyes.close();
    }
    if (configuration.isGalenEnabled()) {
      LOG.info("Generating {} Galen reports", galenTests.size());
      new HtmlReportBuilder()
          .build(
              galenTests,
              "target/galen-html-reports/"
                  + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
    }
  }
}
