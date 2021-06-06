Feature: Google search
	
@web @integration
Scenario: Search for Cucumber
	Given I am on the homepage 
	When I search for "cucumber"
	Then I can see search results
