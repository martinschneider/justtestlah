package qa.justtestlah.applitools;

import com.applitools.eyes.selenium.Eyes;
import org.springframework.beans.factory.annotation.Value;

public class Applitools implements qa.justtestlah.stubs.Applitools {

  @Value("${eyes.apiKey}")
  private String eyesApiKey;

  private Eyes eyes;

  public Applitools() {
    Eyes eyes = new Eyes();
    eyes.setApiKey(eyesApiKey);
  }

  @Override
  public void checkWindow() {
    eyes.checkWindow();
  }
}
