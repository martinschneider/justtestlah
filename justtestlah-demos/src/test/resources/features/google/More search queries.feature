Feature: Google search

#@web @integration
Scenario: Search for Selenium
	Given I am on the homepage 
	When I search for "selenium"
	Then I can see search results
