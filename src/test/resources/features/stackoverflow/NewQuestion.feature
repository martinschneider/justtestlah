@android
Feature: New question

Scenario: Ask a question using quick link
	Given I am on the homepage 
	# this step requires OpenCV (enable it using `opencv.enabled=true` in `yasew.properties`)
	Then I can see the ask a question icon