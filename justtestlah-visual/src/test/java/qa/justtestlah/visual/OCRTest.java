package qa.justtestlah.visual;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class OCRTest {

  private OCR target;

  // @Test // disabled during CI
  public void helloWorldTest() throws TesseractException {
    TakesScreenshot driver = mock(TakesScreenshot.class);
    target = new OCR(new Tesseract()).withDriver(driver);
    when(driver.getScreenshotAs(OutputType.FILE)).thenReturn(getPath("helloworld.png"));
    assertThat(target.getText().trim()).isEqualTo("hello world");
  }

  private File getPath(String fileName) {
    return new File(this.getClass().getClassLoader().getResource("images/" + fileName).getFile());
  }
}
