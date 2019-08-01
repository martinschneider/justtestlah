package qa.justtestlah.examples.stackoverflow.pages;

import static com.codeborne.selenide.Selenide.sleep;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import qa.justtestlah.base.BasePage;
import qa.justtestlah.configuration.Platform;

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
    sleep(5000); // TODO: explicitly check something
    return this;
  }

  public QuestionsPage selectTag(String tagName) {
    $("TAG_SELECT").click();
    return questions;
  }
}
