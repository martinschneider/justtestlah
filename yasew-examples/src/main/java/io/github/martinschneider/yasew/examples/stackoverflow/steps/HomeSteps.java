package io.github.martinschneider.yasew.examples.stackoverflow.steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.github.martinschneider.yasew.base.BaseSteps;
import io.github.martinschneider.yasew.examples.stackoverflow.pages.HomePage;

import static org.assertj.core.api.Assertions.assertThat;

public class HomeSteps extends BaseSteps {
	private HomePage home;

	@Given("^I am on the homepage$")
	public void homepage() {
		home.load();
	}

	@When("^I go to the tags page")
	public void goToTags() {
		home.navigateToTagsPage();
	}

	@When("I search for \"([^\"]*)\"")
	public void search(String query) {
		home.search(query);
	}
	
	@Then("^I can see the ask a question icon$")
	public void questionIcon()
	{
		assertThat(home.hasImage("questionIcon.png")).as("check that question icon is present").isTrue();
		assertThat(home.hasImage("facebook.png")).as("check that question icon is present").isFalse();
	}
}
