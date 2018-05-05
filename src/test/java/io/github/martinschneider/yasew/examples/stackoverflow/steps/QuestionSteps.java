package io.github.martinschneider.yasew.examples.stackoverflow.steps;

import static org.assertj.core.api.Assertions.assertThat;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import io.github.martinschneider.yasew.base.BaseSteps;
import io.github.martinschneider.yasew.examples.stackoverflow.pages.QuestionPage;
import io.github.martinschneider.yasew.examples.stackoverflow.pages.QuestionsPage;

public class QuestionSteps extends BaseSteps {
  private QuestionPage questionPage;

  private QuestionsPage questionsPage;

  @Given("^I select the first question$")
  public void selectFirstQuestion() {
    questionsPage.openFirstQuestion();
  }

  @Then("the question is tagged with \"([^\"]*)\"")
  public void isQuestionTaggedWith(String tagName) {
    assertThat(questionPage.hasTag(tagName))
        .as("Check that tag " + tagName + " is present")
        .isTrue();
  }
}
