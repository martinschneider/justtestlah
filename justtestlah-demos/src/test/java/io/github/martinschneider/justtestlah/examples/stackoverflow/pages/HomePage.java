package io.github.martinschneider.justtestlah.examples.stackoverflow.pages;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Selenide.open;

import io.github.martinschneider.justtestlah.base.BasePage;
import io.github.martinschneider.justtestlah.configuration.Platform;
import org.openqa.selenium.WebElement;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Platform.WEB)
public class HomePage extends BasePage<HomePage> {

  private QuestionsPage questions;

  private TagsPage tags;

  public HomePage load() {
    open(configuration.getBaseUrl());
    WebElement dismissButton = $("GDPR_CONSENT_DISMISS");
    if (dismissButton.isDisplayed()) {
      dismissButton.click();
    }
    return this;
  }

  public TagsPage navigateToTagsPage() {
    $("MENU_TAGS").click();
    return tags;
  }

  /**
   * Perform a search.
   *
   * @param query search query
   * @return {@link QuestionsPage}
   */
  public QuestionsPage search(String query) {
    $("SEARCH_FIELD").sendKeys(query);
    $("SEARCH_BUTTON").should(appear).click();
    return questions;
  }
}
