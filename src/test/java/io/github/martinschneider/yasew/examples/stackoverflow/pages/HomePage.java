package io.github.martinschneider.yasew.examples.stackoverflow.pages;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Selenide.open;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import io.github.martinschneider.yasew.base.BasePage;
import io.github.martinschneider.yasew.configuration.Platform;

@Component
@Profile(Platform.WEB)
public class HomePage extends BasePage<HomePage> {

  private QuestionsPage questions;

  private TagsPage tags;

  public HomePage load() {
    open(configuration.getBaseUrl());
    return this;
  }

  public TagsPage navigateToTagsPage() {
    $("MENU_TAGS").click();
    return tags;
  }

  public QuestionsPage search(String query) {
    $("SEARCH_FIELD").sendKeys(query);
    $("SEARCH_BUTTON").should(appear).click();
    return questions;
  }
}
