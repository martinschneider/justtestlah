package io.github.martinschneider.justtestlah.examples.stackoverflow.pages.android;

import static com.codeborne.selenide.Selenide.sleep;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.codeborne.selenide.WebDriverRunner;

import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.offset.PointOption;
import io.github.martinschneider.justtestlah.configuration.Platform;
import io.github.martinschneider.justtestlah.examples.stackoverflow.pages.HomePage;
import io.github.martinschneider.justtestlah.examples.stackoverflow.pages.QuestionsPage;
import io.github.martinschneider.justtestlah.visual.Match;

@Component
@Profile({Platform.ANDROID})
public class AndroidHomePage extends HomePage {
  private QuestionsPage questionsPage;

  @Override
  public AndroidHomePage load() {
    return this;
  }

  @Override
  public QuestionsPage search(String query) {
    $("SEARCH_FIELD").sendKeys(query + "\n");
    sleep(5000);
    return questionsPage;
  }

  public void tapOnQuestionIcon() {
    Match questionIcon = findImage("questionIcon.png", 0.85);
    if (questionIcon.isFound()) {
      new TouchAction((PerformsTouchActions) WebDriverRunner.getWebDriver())
          .tap(PointOption.point(questionIcon.getX(), questionIcon.getY()))
          .perform();
    }
  }
}
