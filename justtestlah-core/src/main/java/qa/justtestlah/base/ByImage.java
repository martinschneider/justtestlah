package qa.justtestlah.base;

import com.codeborne.selenide.WebDriverRunner;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import qa.justtestlah.stubs.OCR;
import qa.justtestlah.stubs.TemplateMatcher;
import qa.justtestlah.utils.ImageUtils;

public class ByImage extends By {

  public static final double DEFAULT_THRESHOLD = 0.8;
  private String imageName;
  private double threshold;
  private TemplateMatcher templateMatcher;
  private OCR ocr;

  protected ByImage(String imageName, double threshold, TemplateMatcher templateMatcher, OCR ocr) {
    this.imageName = imageName;
    this.threshold = threshold;
    this.templateMatcher = templateMatcher;
    this.ocr = ocr;
  }

  public static By image(String imageName, TemplateMatcher templateMatcher, OCR ocr) {
    return new ByImage(imageName, DEFAULT_THRESHOLD, templateMatcher, ocr);
  }

  public static By image(
      String imageName, double threshold, TemplateMatcher templateMatcher, OCR ocr) {
    return new ByImage(imageName, threshold, templateMatcher, ocr);
  }

  @Override
  public List<WebElement> findElements(SearchContext context) {
    WebDriver driver = WebDriverRunner.getWebDriver();
    if (driver instanceof TakesScreenshot) {
      File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
      String path = ImageUtils.getFullPath(imageName);
      if (path == null) {
        throw new WebDriverException(
            String.format(
                "Template image %s not found on classpath. Please make sure it is located in the images folder",
                imageName));
      }
      List<WebElement> list = new ArrayList<>();
      list.add(
          new ImageWebElement(
              driver,
              templateMatcher.match(screenshotFile.getAbsolutePath(), path, threshold).getRect(),
              ocr,
              path));
      return list;
    } else {
      throw new UnsupportedOperationException(
          "This operation is not supported for the current WebDriver: "
              + driver.getClass().getSimpleName()
              + ".");
    }
  }
}
