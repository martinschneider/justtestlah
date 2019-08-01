package qa.justtestlah.examples.stackoverflow.pages;

import static com.codeborne.selenide.Condition.appear;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import qa.justtestlah.base.BasePage;
import qa.justtestlah.configuration.Platform;

@Component
@Profile({Platform.WEB, Platform.ANDROID, Platform.IOS})
public class QuestionPage extends BasePage<QuestionPage> {
  public boolean hasTag(String tagName) {
    return $("POST_TAG", tagName).should(appear).isDisplayed();
  }
}
