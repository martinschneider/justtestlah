package qa.justtestlah.configuration;

import org.openqa.selenium.WebDriver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Factory for {@link WebDriver}. */
@Component
@ConditionalOnProperty(
    value = "cloudprovider",
    havingValue = "awsdevicefarm",
    matchIfMissing = true)
public class MockWebDriverBuilder implements WebDriverBuilder {

  @Override
  public WebDriver getAndroidDriver() {
    return null;
  }

  @Override
  public WebDriver getIOsDriver() {
    return null;
  }

  @Override
  public WebDriver getWebDriver() {
    return null;
  }
}
