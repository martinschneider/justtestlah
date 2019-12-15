package qa.justtestlah.examples.stackoverflow.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import qa.justtestlah.base.BaseSteps;
import qa.justtestlah.examples.stackoverflow.pages.TagsPage;

public class TagSteps extends BaseSteps {
  private TagsPage tagsPage;

  @Given("I filter for {string}")
  public void filter(String filter) {
    tagsPage.filterTags(filter);
  }

  @When("I select the tag {string}")
  public void selectTag(String tagName) {
    tagsPage.selectTag(tagName);
  }
}
