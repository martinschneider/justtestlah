package io.github.martinschneider.justtestlah.examples.stackoverflow.pages;

import static com.codeborne.selenide.Selenide.sleep;

import io.github.martinschneider.justtestlah.base.BasePage;
import io.github.martinschneider.justtestlah.configuration.Platform;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({Platform.WEB, Platform.ANDROID})
public class TagsPage extends BasePage<TagsPage> {

  private QuestionsPage questions;

  /**
   * Filter questions.
   *
   * @param filter search filter
   * @return {@link TagsPage}
   */
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
