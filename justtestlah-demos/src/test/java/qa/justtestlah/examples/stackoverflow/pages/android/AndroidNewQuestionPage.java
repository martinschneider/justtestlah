package qa.justtestlah.examples.stackoverflow.pages.android;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import qa.justtestlah.configuration.Platform;
import qa.justtestlah.examples.stackoverflow.pages.NewQuestionPage;

@Component
@Profile({Platform.ANDROID})
public class AndroidNewQuestionPage extends NewQuestionPage {
  public boolean isDisplayed() {
    return $("QUESTION_TITLE").isDisplayed();
  }
}
