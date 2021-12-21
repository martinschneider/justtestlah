package qa.justtestlah.examples.google.steps;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebElement;
import qa.justtestlah.base.BaseSteps;
import qa.justtestlah.examples.google.model.Search;
import qa.justtestlah.examples.google.pages.GooglePage;
import qa.justtestlah.examples.google.pages.ResultsPage;

public class GoogleSteps extends BaseSteps {
  private GooglePage google;
  private ResultsPage results;

  @Given("I am on the homepage")
  public void homepage() {
    google.verify();
  }

  @Then("I can see the Google logo")
  public void checkLogo() {
    WebElement logo = google.getLogo();
    assertThat(logo.isDisplayed()).as("Google logo is visible").isTrue();
    assertThat(logo.getText()).as("Google logo spells Google").isEqualTo("Google");
  }

  @When("I search for {string}")
  public void search(String key) {
    google.search(testdata(Search.class, key)).verify();
  }

  @Then("I can see search results")
  public void resultsPage() {
    results.verify();
  }

  @When("I click on the Google logo")
  public void clickLogo() {
    results.clickLogo();
  }
}
