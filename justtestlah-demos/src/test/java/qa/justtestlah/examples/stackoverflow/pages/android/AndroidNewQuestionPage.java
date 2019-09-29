package qa.justtestlah.examples.stackoverflow.pages.android;

import static qa.justtestlah.configuration.Platform.Constants.ANDROID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import qa.justtestlah.examples.stackoverflow.pages.NewQuestionPage;

@Component
@Profile(ANDROID)
public class AndroidNewQuestionPage extends NewQuestionPage {
  public boolean isDisplayed() {
    return $("QUESTION_TITLE").isDisplayed();
  }
}
