package qa.justtestlah.galen.hooks;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.galenframework.reports.HtmlReportBuilder;
import io.cucumber.java.Scenario;
import qa.justtestlah.configuration.JustTestLahConfiguration;
import qa.justtestlah.exception.JustTestLahException;
import qa.justtestlah.galen.GalenTestInfoContainer;
import qa.justtestlah.hooks.AbstractCucumberHook;
import qa.justtestlah.hooks.HooksRegister;

/** Galen hooks. */
@Component
public class GalenHooks extends AbstractCucumberHook implements InitializingBean {

  private static final String GALEN_REPORT_FOLDER_DATE_PATTERN = "yyyy-MM-dd HH.mm.ss";

  private static final Logger LOG = LoggerFactory.getLogger(GalenHooks.class);

  @Autowired private JustTestLahConfiguration configuration;

  @Autowired private GalenTestInfoContainer galenTests;

  @Autowired private HooksRegister hooksRegister;

  @Override
  public void afterPropertiesSet() {
    hooksRegister.addHooks(this);
  }
  
  /**
   * Generate Galen reports.
   *
   * @param scenario Cucumber scenario
   */
  @Override
  public void after(Scenario scenario) {
    if (configuration.isGalenEnabled()) {
      LOG.info("Generating {} Galen reports", galenTests.get().size());
      try {
        new HtmlReportBuilder().build(galenTests.get(), getGalenReportDirectory());
      } catch (IOException exception) {
        throw new JustTestLahException("Error generating Galen reports.", exception);
      }
    }
  }

  private String getGalenReportDirectory() {
    return configuration.getGalenReportDirectory()
        + File.separator
        + LocalDateTime.now().format(DateTimeFormatter.ofPattern(GALEN_REPORT_FOLDER_DATE_PATTERN));
  }
}
