@android
Feature: New question

Scenario: Ask a question using quick link
	Given I am on the homepage 
	# this step requires OpenCV template matching
	Then I can see the ask a question icon