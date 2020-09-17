package qa.justtestlah.stubs;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public interface OCR {
  void setDriver(WebDriver driver);

  String getText(WebElement element);
}
