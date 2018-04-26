package io.github.martinschneider.yasew.examples.stackoverflow.pages.android;

import static com.codeborne.selenide.Selenide.sleep;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import io.github.martinschneider.yasew.configuration.Platform;
import io.github.martinschneider.yasew.examples.stackoverflow.pages.HomePage;
import io.github.martinschneider.yasew.examples.stackoverflow.pages.QuestionsPage;

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
