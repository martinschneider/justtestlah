package qa.justtestlah.integration.steps;

import io.cucumber.java.en.Given;
import qa.justtestlah.base.BaseSteps;
import qa.justtestlah.integration.model.SearchTerm;
import qa.justtestlah.integration.pages.GooglePage;

public class GoogleSteps extends BaseSteps {
  private GooglePage google;

  @Given("I am on the homepage")
  public void homepage() {
    google.verify().enterSearchTerm(testdata(SearchTerm.class));
  }
}
