package io.github.martinschneider.justtestlah.examples.stackoverflow.pages.android;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import io.github.martinschneider.justtestlah.base.BasePage;
import io.github.martinschneider.justtestlah.configuration.Platform;

@Component
@Profile({Platform.ANDROID})
public class AndroidNewQuestionPage extends BasePage<AndroidNewQuestionPage> {
  public boolean isDisplayed() {
    return $("QUESTION_TITLE").isDisplayed();
  }
}
