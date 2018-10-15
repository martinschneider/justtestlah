package io.github.martinschneider.justtestlah.examples.stackoverflow.steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import io.github.martinschneider.justtestlah.base.BaseSteps;
import io.github.martinschneider.justtestlah.examples.stackoverflow.pages.TagsPage;

public class TagSteps extends BaseSteps {
  private TagsPage tagsPage;

  @Given("^I filter for \"([^\"]*)\"$")
  public void filter(String filter) {
    tagsPage.filterTags(filter);
  }

  @When("^I select the tag \"([^\"]*)\"$")
  public void selectTag(String tagName) {
    tagsPage.selectTag(tagName);
  }
}
