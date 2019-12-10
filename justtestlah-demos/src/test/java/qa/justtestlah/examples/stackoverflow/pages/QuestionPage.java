package qa.justtestlah.examples.stackoverflow.pages;

import static com.codeborne.selenide.Condition.appear;
import static qa.justtestlah.configuration.Platform.Constants.ANDROID;
import static qa.justtestlah.configuration.Platform.Constants.IOS;
import static qa.justtestlah.configuration.Platform.Constants.WEB;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import qa.justtestlah.base.BasePage;

@Component
@Profile({WEB, ANDROID, IOS})
public class QuestionPage extends BasePage<QuestionPage> {
  public boolean hasTag(String tagName) {
    return $("POST_TAG", tagName).should(appear).isDisplayed();
  }
}
