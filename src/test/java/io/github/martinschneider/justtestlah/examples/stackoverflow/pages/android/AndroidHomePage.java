package io.github.martinschneider.justtestlah.examples.stackoverflow.pages.android;

import static com.codeborne.selenide.Selenide.sleep;

import io.github.martinschneider.justtestlah.configuration.Platform;
import io.github.martinschneider.justtestlah.examples.stackoverflow.pages.HomePage;
import io.github.martinschneider.justtestlah.examples.stackoverflow.pages.QuestionsPage;
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
}
