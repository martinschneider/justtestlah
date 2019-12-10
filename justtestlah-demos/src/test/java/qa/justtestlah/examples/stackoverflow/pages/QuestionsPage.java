package qa.justtestlah.examples.stackoverflow.pages;

import static qa.justtestlah.configuration.Platform.Constants.ANDROID;
import static qa.justtestlah.configuration.Platform.Constants.IOS;
import static qa.justtestlah.configuration.Platform.Constants.WEB;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import qa.justtestlah.base.BasePage;

@Component
@Profile({WEB, ANDROID, IOS})
public class QuestionsPage extends BasePage<QuestionsPage> {

  private QuestionPage question;

  public QuestionPage openFirstQuestion() {
    $("QUESTION_LINK").click();
    return question;
  }
}
