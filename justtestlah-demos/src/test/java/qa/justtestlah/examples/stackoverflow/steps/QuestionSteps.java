package qa.justtestlah.examples.stackoverflow.steps;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import qa.justtestlah.base.BaseSteps;
import qa.justtestlah.examples.stackoverflow.pages.QuestionPage;
import qa.justtestlah.examples.stackoverflow.pages.QuestionsPage;

public class QuestionSteps extends BaseSteps {
  private QuestionPage questionPage;

  private QuestionsPage questionsPage;

  @Given("I select the first question")
  public void selectFirstQuestion() {
    questionsPage.openFirstQuestion();
  }

  /**
   * Check whether a question is tagged with a given tag.
   *
   * @param tagName name of the tag
   */
  @Then("the question is tagged with {string}")
  public void isQuestionTaggedWith(String tagName) {
    assertThat(questionPage.hasTag(tagName))
        .as("Check that tag " + tagName + " is present")
        .withFailMessage("Tag %s is missing", tagName)
        .isTrue();
  }
}
