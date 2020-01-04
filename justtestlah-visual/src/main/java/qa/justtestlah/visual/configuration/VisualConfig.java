package qa.justtestlah.visual.configuration;

import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import qa.justtestlah.configuration.JustTestLahConfig;

/** JustTestLah! Visual Spring context. */
@Configuration
@Import(JustTestLahConfig.class)
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
