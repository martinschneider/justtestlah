package io.github.martinschneider.justtestlah.examples.stackoverflow.pages;

import io.github.martinschneider.justtestlah.base.BasePage;
import io.github.martinschneider.justtestlah.configuration.Platform;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({Platform.WEB, Platform.ANDROID, Platform.IOS})
public class QuestionsPage extends BasePage<QuestionsPage> {

  private QuestionPage question;

  public QuestionPage openFirstQuestion() {
    $("QUESTION_LINK").click();
    return question;
  }
}
