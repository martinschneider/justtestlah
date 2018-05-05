package io.github.martinschneider.yasew.examples.stackoverflow.steps;

import static org.assertj.core.api.Assertions.assertThat;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.github.martinschneider.yasew.base.BaseSteps;
import io.github.martinschneider.yasew.examples.stackoverflow.pages.HomePage;

public class HomeSteps extends BaseSteps {
  private HomePage home;

  @Given("^I am on the homepage$")
  public void homepage() {
    home.load();
  }

  @When("^I go to the tags page")
  public void goToTags() {
    home.navigateToTagsPage();
  }

  @When("I search for \"([^\"]*)\"")
  public void search(String query) {
    home.search(query);
  }

  @Then("^I can see the ask a question icon$")
  public void questionIcon() {
    assertThat(home.hasImage("questionIcon.png"))
        .as("check that question icon is present")
        .isTrue();
    assertThat(home.hasImage("questionIcon_blurred.png"))
        .as("check that blurred question icon is present")
        .isTrue();
    assertThat(home.hasImage("questionIcon_rotated.png", 0.85))
        .as("check that rotated question icon is present")
        .isTrue();
    assertThat(home.hasImage("questionIcon_distorted.png", 0.75))
        .as("check that distorted question icon is present")
        .isTrue();
    assertThat(home.hasImage("facebook.png"))
        .as("check that facebook icon is not present")
        .isFalse();
  }
}
