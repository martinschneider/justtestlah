package qa.justtestlah.examples.stackoverflow.pages.android;

import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.Condition.appear;
import static qa.justtestlah.configuration.Platform.Constants.ANDROID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import qa.justtestlah.examples.stackoverflow.pages.HomePage;
import qa.justtestlah.examples.stackoverflow.pages.QuestionsPage;

@Component
@Profile(ANDROID)
public class AndroidHomePage extends HomePage {
  private QuestionsPage questionsPage;

  @Override
  public AndroidHomePage load() {
    return this;
  }

  @Override
  public QuestionsPage search(String query) {
    $("SEARCH_FIELD").should(appear).sendKeys(query + "\n");
    sleep(5000);
    return questionsPage;
  }

  public void tapOnQuestionIcon() {
    $("QUESTION_ICON").click();
  }
}
