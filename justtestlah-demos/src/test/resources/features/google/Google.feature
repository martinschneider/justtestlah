Feature: Google search

@web @integration
Scenario: Search 
	Given I am on the homepage 
	#Then I can see the Google logo
	When I search for "default"
	Then I can see search results
	#When I click on the Google logo
	#Then I am on the homepage
	#And I can see the Google logo
