package io.github.martinschneider.yasew.examples.carousell.steps;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.beans.factory.annotation.Autowired;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.github.martinschneider.yasew.base.BaseSteps;
import io.github.martinschneider.yasew.examples.carousell.pages.HomePage;
import io.github.martinschneider.yasew.user.UserService;

public class LoginSteps extends BaseSteps {

  @Autowired private UserService userService;

  private HomePage homePage;

  @When("^I login as \"([^\"]*)\"$")
  public void iLoginAs(String userKey) {
    homePage
        .checkWindow()
        .goToLogin()
        .checkWindow()
        .checkLayout()
        .login(userService.get(userKey))
        .checkWindow();
  }

  @Then("^I see the user menu$")
  public void iSeeTheUserMenu() {
    assertThat(homePage.isUserMenuVisible()).as("user menu is visible").isTrue();
  }
}
