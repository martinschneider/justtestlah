package qa.justtestlah.applitools;

import com.applitools.eyes.selenium.Eyes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class Applitools implements qa.justtestlah.stubs.Applitools {

  @Autowired private Eyes eyes;

  @Override
  public void checkWindow() {
    eyes.checkWindow();
  }
}
