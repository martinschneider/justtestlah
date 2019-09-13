package qa.justtestlah.examples.carousell.steps;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import qa.justtestlah.base.BaseSteps;
import qa.justtestlah.examples.carousell.model.User;
import qa.justtestlah.examples.carousell.pages.HomePage;
import qa.justtestlah.examples.carousell.pages.LoginPage;
import qa.justtestlah.examples.carousell.pages.WelcomePage;

public class LoginSteps extends BaseSteps {

  @Autowired private WelcomePage welcomePage;

  @Autowired private LoginPage loginPage;

  @Autowired private HomePage homePage;

  /**
   * Login the given user.
   *
   * @param userKey userKey of the user to log in
   */
  @When("I login as {string}")
  public void loginAs(String userKey) {
    welcomePage
        .checkWindow()
        .goToLogin()
        .checkWindow()
        .checkLayout()
        .login(testdata(User.class, userKey))
        .checkWindow();
  }

  @Then("I see the sell button")
  public void isSellButtonVisible() {
    assertThat(homePage.isSellButtonVisible()).as("sell button is displayed").isTrue();
  }

  @Then("I see an error message")
  public void isErrorMessageVisible() {
    assertThat(loginPage.isErrorMessageVisible()).as("error message id displayed").isTrue();
  }
}
