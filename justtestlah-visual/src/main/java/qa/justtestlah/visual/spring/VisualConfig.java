package qa.justtestlah.visual.spring;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** JustTestLah! Visual Spring context. */
@Configuration
public class VisualConfig {

  @Value("${tesseract.datapath}")
  private String tesseractDataPath;

  @Bean
  public Tesseract tesseract() {
    Tesseract tess = new Tesseract();
    tess.setDatapath(tesseractDataPath);
    return tess;
  }
}
