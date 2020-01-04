package qa.justtestlah.examples.google.steps;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import qa.justtestlah.base.BaseSteps;
import qa.justtestlah.examples.google.model.Search;
import qa.justtestlah.examples.google.pages.GooglePage;

public class GoogleSteps extends BaseSteps {
  private GooglePage google;

  @Given("I am on the homepage")
  public void homepage() {
    google.verify().enterSearchTerm(testdata(Search.class));
  }

  @Then("the Google logo shows the correct text")
  public void checkLogo() {
    assertThat(google.getLogoText().trim()).isEqualTo("Google");
  }
}
