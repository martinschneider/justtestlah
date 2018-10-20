Feature: Login 

@web @android @ios
Scenario: Successful login 
	When I login as "validUser" 
	Then I see the user menu