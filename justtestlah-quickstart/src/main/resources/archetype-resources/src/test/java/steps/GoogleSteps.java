package steps;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import model.Search;
import pages.GooglePage;
import qa.justtestlah.base.BaseSteps;

public class GoogleSteps extends BaseSteps {

  private GooglePage google;

  @Given("I am on the homepage")
  public void homepage() {
    google.verify();
  }
  
  @When("I search for {}")
  public void search(String searchKey) {
    google.search(testdata(Search.class, searchKey));
  }
  
  @Then("I see search results")
  public void searchResults()
  {
	  assertThat(google.hasSearchResults()).isTrue();
  }
}
