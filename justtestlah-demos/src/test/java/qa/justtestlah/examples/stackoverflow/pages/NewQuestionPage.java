package qa.justtestlah.examples.stackoverflow.pages;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import qa.justtestlah.base.BasePage;
import qa.justtestlah.configuration.Platform;

@Component
@Profile({Platform.WEB})
public class NewQuestionPage extends BasePage<NewQuestionPage> {
  public boolean isDisplayed() {
    throw new NotImplementedException("This is only available for WEB!");
  }
}
