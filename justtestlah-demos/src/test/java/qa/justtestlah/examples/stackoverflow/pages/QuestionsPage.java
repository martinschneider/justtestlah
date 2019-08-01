package qa.justtestlah.examples.stackoverflow.pages;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import qa.justtestlah.base.BasePage;
import qa.justtestlah.configuration.Platform;

@Component
@Profile({Platform.WEB, Platform.ANDROID, Platform.IOS})
public class QuestionsPage extends BasePage<QuestionsPage> {

  private QuestionPage question;

  public QuestionPage openFirstQuestion() {
    $("QUESTION_LINK").click();
    return question;
  }
}
