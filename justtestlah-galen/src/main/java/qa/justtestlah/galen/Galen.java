package qa.justtestlah.galen;

import com.codeborne.selenide.WebDriverRunner;
import com.galenframework.reports.GalenTestInfo;
import com.galenframework.reports.model.LayoutReport;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import qa.justtestlah.configuration.JustTestLahConfiguration;
import qa.justtestlah.galen.exception.UILocatorInjector;
import qa.justtestlah.locator.LocatorMap;

@Component
@Primary
public class Galen implements qa.justtestlah.stubs.Galen {

  private Logger LOG = LoggerFactory.getLogger(Galen.class);

  @Autowired private GalenTestInfoContainer galenTests;

  @Autowired private JustTestLahConfiguration configuration;

  @Autowired private UILocatorInjector uiLocatorInjector;

  @Value("${galen.inject.locators:true}")
  private boolean injectLocators;

  public void checkLayout(String spec, LocatorMap locators) {
    LOG.info("Checking layout {}", spec);
    URL specURL = this.getClass().getClassLoader().getResource(spec);
    if (specURL == null) {
      LOG.warn(
          String.format("Missing Galen specification %s. Skipping checks for this page.", specURL));
      return;
    }
    String specPath = specURL.getPath();
    if (injectLocators) {
      specPath = uiLocatorInjector.injectUILocators(specPath, locators);
    }
    String title = "Check layout " + specPath;
    LayoutReport layoutReport;
    try {
      layoutReport =
          com.galenframework.api.Galen.checkLayout(
              WebDriverRunner.getWebDriver(),
              specPath,
              Collections.singletonList(configuration.getPlatform().name()));
      GalenTestInfo test = GalenTestInfo.fromString(this.getClass().getSimpleName());
      test.getReport().layout(layoutReport, title);
      galenTests.add(test);
    } catch (IOException exception) {
      LOG.warn("Error checking layout", exception);
    }
  }
}
