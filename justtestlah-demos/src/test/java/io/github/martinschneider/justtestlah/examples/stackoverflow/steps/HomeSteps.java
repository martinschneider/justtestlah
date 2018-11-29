package io.github.martinschneider.justtestlah.examples.stackoverflow.steps;

import static org.assertj.core.api.Assertions.assertThat;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.github.martinschneider.justtestlah.base.BaseSteps;
import io.github.martinschneider.justtestlah.examples.stackoverflow.pages.HomePage;
import io.github.martinschneider.justtestlah.examples.stackoverflow.pages.NewQuestionPage;
import io.github.martinschneider.justtestlah.examples.stackoverflow.pages.android.AndroidHomePage;

public class HomeSteps extends BaseSteps {
  private HomePage home;
  private NewQuestionPage askQuestion;

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

  @Then("^I can see the question icon$")
  public void matchQuestionIcon() {
    // The first assertion would be sufficient. We run some more checks to show-case the template
    // matching.
    assertThat(home.hasImage("questionIcon.png")).isEqualTo(true);
    assertThat(home.hasImage("questionIcon_blurred.png")).isEqualTo(true);
    assertThat(home.hasImage("questionIcon_distorted.png", 0.75)).isEqualTo(true);
    assertThat(home.hasImage("questionIcon_rotated.png", 0.85)).isEqualTo(true);
  }

  @Then("^I can't see a Facebook icon$")
  public void noFacebook() {
    assertThat(home.hasImage("facebook.png")).isEqualTo(false);
  }

  @When("^I click on the question icon$")
  public void questionIcon() {
    // this step is platform-dependent
    ((AndroidHomePage) home).tapOnQuestionIcon();
  }

  @Then("^I can enter a new question$")
  public void checkQuestionPage() {
    assertThat(askQuestion.isDisplayed()).as("check for ask question page").isEqualTo(true);
  }
}
