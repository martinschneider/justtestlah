Feature: Google search

@web
Scenario: Search 
	Given I am on the homepage
	When I search for default
	Then I see search results
