package qa.justtestlah.base;

import java.io.File;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import qa.justtestlah.stubs.OCR;

public class ImageWebElement implements WebElement {

  public ImageWebElement(WebDriver driver, Rectangle rect, OCR ocr, String path) {
    super();
    this.driver = driver;
    this.rect = rect;
    this.ocr = ocr;
    this.path = path;
  }

  protected WebDriver driver;
  protected Rectangle rect;
  protected OCR ocr;
  protected String path;

  @Override
  public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
    return (X) new File(path);
  }

  @Override
  public void click() {
    int x = rect.x + rect.width / 2;
    int y = rect.y + rect.height / 2;
    if (driver instanceof JavascriptExecutor) {
      ((JavascriptExecutor) driver)
          .executeScript(
              String.format("el = document.elementFromPoint(%d, %d); el.click();", x, y));
    }
  }

  @Override
  public void submit() {
    throw new UnsupportedOperationException("operation not supported");
  }

  @Override
  public void sendKeys(CharSequence... keysToSend) {
    throw new UnsupportedOperationException("operation not supported");
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException("operation not supported");
  }

  @Override
  public String getTagName() {
    // TODO: not sure what to return here
    return "IMG";
  }

  @Override
  public String getAttribute(String name) {
    throw new UnsupportedOperationException("operation not supported");
  }

  @Override
  public boolean isSelected() {
    throw new UnsupportedOperationException("operation not supported");
  }

  @Override
  public boolean isEnabled() {
    throw new UnsupportedOperationException("operation not supported");
  }

  @Override
  public String getText() {
    return ocr.getText(this);
  }

  @Override
  public List<WebElement> findElements(By by) {
    throw new UnsupportedOperationException("operation not supported");
  }

  @Override
  public WebElement findElement(By by) {
    throw new UnsupportedOperationException("operation not supported");
  }

  @Override
  public boolean isDisplayed() {
    return rect != null;
  }

  @Override
  public Point getLocation() {
    return new Point(rect.x, rect.y);
  }

  @Override
  public Dimension getSize() {
    return new Dimension(rect.width, rect.height);
  }

  @Override
  public Rectangle getRect() {
    return rect;
  }

  @Override
  public String getCssValue(String propertyName) {
    throw new UnsupportedOperationException("operation not supported");
  }
}
