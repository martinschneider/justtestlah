package io.github.martinschneider.justtestlah.steps;

import com.galenframework.reports.GalenTestInfo;
import com.galenframework.reports.HtmlReportBuilder;
import cucumber.api.java.After;
import io.github.martinschneider.justtestlah.configuration.JustTestLahConfiguration;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/** Hook to restart the WebDriver before every test. */
public class GalenHooks {

  private static final String GALEN_REPORT_FOLDER_DATE_PATTERN = "yyyy-MM-dd HH.mm.ss";

  private static final Logger LOG = LoggerFactory.getLogger(GalenHooks.class);

  @Autowired private JustTestLahConfiguration configuration;

  @Autowired private List<GalenTestInfo> galenTests;

  /**
   * Close the web driver and Applitools. Generate Galen reports.
   *
   * @throws IOException {@link IOException}
   */
  @After
  public void createReports() throws IOException {
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
