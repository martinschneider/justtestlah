package io.github.martinschneider.yasew.examples.stackoverflow.pages;

import io.github.martinschneider.yasew.base.BasePage;
import io.github.martinschneider.yasew.configuration.Platform;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({Platform.WEB, Platform.ANDROID, Platform.IOS})
public class QuestionPage extends BasePage<QuestionPage> {
  public boolean hasTag(String tagName) {
    return $("POST_TAG", tagName).isDisplayed();
  }
}
