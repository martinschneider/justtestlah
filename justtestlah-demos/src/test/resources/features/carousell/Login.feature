Feature: Login 

@web @android @ios
Scenario: Successful login 
	When I login as "valid" 
	Then I see the sell button

@web @android @ios
Scenario: Failed login
	When I login as "invalid" 
	Then I see an error message