package io.github.martinschneider.yasew.examples.carousell.steps;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.github.martinschneider.yasew.base.BaseSteps;
import io.github.martinschneider.yasew.examples.carousell.pages.HomePage;
import io.github.martinschneider.yasew.examples.carousell.pages.LoginPage;
import io.github.martinschneider.yasew.user.UserService;

public class LoginSteps extends BaseSteps {
	
	@Autowired
	private UserService userService;
	
	private LoginPage loginPage;
	
	private HomePage homePage;
	
	@When("^I login as \"([^\"]*)\"$")
    public void iLoginAs(String userKey) throws InterruptedException {
		homePage.goToLogin().login(userService.get(userKey));
    }
	
	@Then("^I see the user menu$")
	public void iSeeTheUserMenu()
	{
		assertThat(homePage.isUserMenuVisible()).as("user menu is visible").isTrue();
	}
	
	@Then("^I see an error message$")
	public void iSeeAnErrorMessage()
	{
		assertThat(loginPage.isErrorMessagePresent()).as("error message is displayed").isTrue();
	}
	
}
