package qa.justtestlah.galen;

import com.codeborne.selenide.WebDriverRunner;
import com.galenframework.reports.GalenTestInfo;
import com.galenframework.reports.model.LayoutReport;
import java.io.IOException;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import qa.justtestlah.configuration.Platform;

public class Galen implements qa.justtestlah.stubs.Galen {

  private Logger LOG = LoggerFactory.getLogger(Galen.class);

  @Autowired private GalenTestInfoContainer galenTests;

  public void checkLayout(String specPath, Platform platform) {
    LOG.info("Checking layout {}", specPath);
    String title = "Check layout " + specPath;
    LayoutReport layoutReport;
    try {
      layoutReport =
          com.galenframework.api.Galen.checkLayout(
              WebDriverRunner.getWebDriver(),
              this.getClass().getClassLoader().getResource(specPath).getPath(),
              Collections.singletonList(platform.name()));
      GalenTestInfo test = GalenTestInfo.fromString(this.getClass().getSimpleName());
      test.getReport().layout(layoutReport, title);
      galenTests.add(test);
    } catch (IOException exception) {
      LOG.warn("Error checking layout", exception);
    }
  }
}
