package io.github.martinschneider.justtestlah.examples.stackoverflow.pages.android;

import static com.codeborne.selenide.Selenide.sleep;

import com.codeborne.selenide.WebDriverRunner;
import io.appium.java_client.HasSettings;
import io.appium.java_client.Setting;
import io.github.martinschneider.justtestlah.configuration.Platform;
import io.github.martinschneider.justtestlah.examples.stackoverflow.pages.HomePage;
import io.github.martinschneider.justtestlah.examples.stackoverflow.pages.QuestionsPage;
import org.openqa.selenium.WebElement;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

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
    WebElement element = $("QUESTION_ICON");
    LOG.info(element.getLocation().toString());
    ((HasSettings) WebDriverRunner.getWebDriver())
        .setSetting(Setting.FIX_IMAGE_FIND_SCREENSHOT_DIMENSIONS, false);
    $("QUESTION_ICON").click();
  }
}
