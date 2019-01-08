package io.github.martinschneider.justtestlah.examples.stackoverflow.pages.android;

import io.github.martinschneider.justtestlah.configuration.Platform;
import io.github.martinschneider.justtestlah.examples.stackoverflow.pages.NewQuestionPage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({Platform.ANDROID})
public class AndroidNewQuestionPage extends NewQuestionPage {
  public boolean isDisplayed() {
    return $("QUESTION_TITLE").isDisplayed();
  }
}
