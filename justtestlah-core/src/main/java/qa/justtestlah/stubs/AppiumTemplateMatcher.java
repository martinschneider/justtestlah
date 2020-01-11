package qa.justtestlah.stubs;

import org.openqa.selenium.WebDriver;

public interface AppiumTemplateMatcher extends TemplateMatcher {
  void setDriver(WebDriver driver);
}
