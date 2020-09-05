Feature: Google search

@web @integration
Scenario: Search 
	Given I am on the homepage 
	Then the Google logo is displayed
	And the Google logo shows the correct text
