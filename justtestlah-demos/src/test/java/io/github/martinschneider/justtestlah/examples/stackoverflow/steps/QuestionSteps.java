package io.github.martinschneider.justtestlah.examples.stackoverflow.steps;

import static org.assertj.core.api.Assertions.assertThat;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import io.github.martinschneider.justtestlah.base.BaseSteps;
import io.github.martinschneider.justtestlah.examples.stackoverflow.pages.QuestionPage;
import io.github.martinschneider.justtestlah.examples.stackoverflow.pages.QuestionsPage;

public class QuestionSteps extends BaseSteps {
  private QuestionPage questionPage;

  private QuestionsPage questionsPage;

  @Given("^I select the first question$")
  public void selectFirstQuestion() {
    questionsPage.openFirstQuestion();
  }

  /**
   * Check whether a question is tagged with a given tag.
   *
   * @param tagName name of the tag
   */
  @Then("the question is tagged with \"([^\"]*)\"")
  public void isQuestionTaggedWith(String tagName) {
    assertThat(questionPage.hasTag(tagName))
        .as("Check that tag " + tagName + " is present")
        .isTrue();
  }
}
