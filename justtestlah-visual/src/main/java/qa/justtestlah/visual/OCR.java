package qa.justtestlah.visual;

import java.io.File;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * OCR (Optical character recognition) capabilities (using Tesseract)
 *
 * <p>http://tess4j.sourceforge.net
 */
@Component
@Primary
public class OCR implements qa.justtestlah.stubs.OCR {

  private Logger LOG = LoggerFactory.getLogger(OCR.class);

  private TakesScreenshot driver;
  private Tesseract ocr;

  @Autowired
  public OCR(Tesseract ocr) {
    this.ocr = ocr;
  }

  /**
   * @param element {@link WebElement} element to perform OCR on
   * @return recognised text of the element
   */
  @Override
  public String getText(WebElement element) {
    return getText(element.getScreenshotAs(OutputType.FILE));
  }

  /** @return all text recognised on the screen */
  public String getText() {
    return getText(getScreenshot());
  }

  private String getText(File file) {
    LOG.info("Peforming OCR on file {}", file);
    try {
      String text = ocr.doOCR(file).trim();
      LOG.info("Found text: {}", text);
      return text;
    } catch (TesseractException exception) {
      LOG.warn("Error performing OCR", exception);
      return null;
    }
  }

  /**
   * Usage:
   *
   * <pre>
   * new OCR().withDriver(driver);
   * </pre>
   *
   * @param driver {@link WebDriver} to use for capturing screenshots
   * @return this
   */
  public OCR withDriver(WebDriver driver) {
    this.driver = (TakesScreenshot) driver;
    return this;
  }

  /**
   * Usage:
   *
   * <pre>
   * new OCR().withDriver(driver);
   * </pre>
   *
   * @param driver {@link TakesScreenshot} to use for capturing screenshots
   * @return this
   */
  public OCR withDriver(TakesScreenshot driver) {
    this.driver = driver;
    return this;
  }

  private File getScreenshot() {
    return driver.getScreenshotAs(OutputType.FILE);
  }

  @Override
  public void setDriver(WebDriver driver) {
    this.driver = (TakesScreenshot) driver;
  }
}
