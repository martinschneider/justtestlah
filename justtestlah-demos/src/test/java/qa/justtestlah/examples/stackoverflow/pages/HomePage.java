package qa.justtestlah.examples.stackoverflow.pages;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Selenide.open;
import static qa.justtestlah.configuration.Platform.Constants.WEB;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import qa.justtestlah.base.BasePage;

@Component
@Profile(WEB)
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
    $("MENU_TOGGLE").click();
    $("MENU_TAGS").should(appear).click();
    return tags;
  }

  /**
   * Perform a search.
   *
   * @param query search query
   * @return {@link QuestionsPage}
   */
  public QuestionsPage search(String query) {
    $("SEARCH_FIELD").sendKeys(query + Keys.RETURN);
    return questions;
  }
}
