package io.github.martinschneider.yasew.examples.stackoverflow.pages;

import static com.codeborne.selenide.Selenide.sleep;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import io.github.martinschneider.yasew.base.BasePage;
import io.github.martinschneider.yasew.configuration.Platform;

@Component
@Profile({Platform.WEB, Platform.ANDROID})
public class TagsPage extends BasePage<TagsPage> {

  private QuestionsPage questions;

  public TagsPage filterTags(String filter) {
    $("TAG_FILTER").sendKeys(filter);
    sleep(5000);
    return this;
  }

  public QuestionsPage selectTag(String tagName) {
    $("TAG_SELECT").click();
    return questions;
  }
}
