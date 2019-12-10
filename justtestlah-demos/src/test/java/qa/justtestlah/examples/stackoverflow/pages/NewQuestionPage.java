package qa.justtestlah.examples.stackoverflow.pages;

import static qa.justtestlah.configuration.Platform.Constants.WEB;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import qa.justtestlah.base.BasePage;

@Component
@Profile(WEB)
public class NewQuestionPage extends BasePage<NewQuestionPage> {
  public boolean isDisplayed() {
    throw new NotImplementedException("This is only available for WEB!");
  }
}
