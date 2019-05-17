package io.github.martinschneider.justtestlah.examples.carousell.steps;

import static org.assertj.core.api.Assertions.assertThat;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.github.martinschneider.justtestlah.base.BaseSteps;
import io.github.martinschneider.justtestlah.examples.carousell.model.User;
import io.github.martinschneider.justtestlah.examples.carousell.pages.HomePage;

public class LoginSteps extends BaseSteps {

  private HomePage homePage;

  /**
   * Login the given user.
   *
   * @param userKey userKey of the user to log in
   */
  @When("^I login as \"([^\"]*)\"$")
  public void loginAs(String userKey) {
    homePage
        .checkWindow()
        .goToLogin()
        .checkWindow()
        .checkLayout()
        .login(testdata(User.class))
        .checkWindow();
  }

  @Then("^I see the user menu$")
  public void isUserMenuVisible() {
    assertThat(homePage.isUserMenuVisible()).as("user menu is visible").isTrue();
  }
}
