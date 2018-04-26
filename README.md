## YASeW - Yet Another Selenium Wrapper

[![Build Status](https://travis-ci.org/martinschneider/yasew.svg?branch=master)](https://travis-ci.org/martinschneider/yasew)
[![Quality gate](https://sonarcloud.io/api/project_badges/measure?project=io.github.martinschneider%3Ayasew&metric=alert_status)](https://sonarcloud.io/dashboard?id=io.github.martinschneider%3Ayasew)
[![Lines of code](https://sonarcloud.io/api/project_badges/measure?project=io.github.martinschneider%3Ayasew&metric=ncloc)](https://sonarcloud.io/component_measures?id=io.github.martinschneider%3Ayasew&metric=ncloc)
[![Code coverage](https://sonarcloud.io/api/project_badges/measure?project=io.github.martinschneider%3Ayasew&metric=coverage)](https://sonarcloud.io/component_measures?id=io.github.martinschneider%3Ayasew&metric=coverage)

YASeW is a JAVA test framework targeting projects that support multiple platforms, in particular Web, Android and iOS. It follows a [BDD](https://martinfowler.com/bliki/GivenWhenThen.html) approach and allows testing against all platforms using the same feature files. YASeW's main aim is to make the configuration as easy and the test code as simple and readable as possible.

### Getting started
Pull the repo and run the example in `yasew-example`. It includes automated tests for [Stack Overflow](https://stackoverflow.com) and [Carousell](http://www.carousell.com).

```bash
git clone https://github.com/martinschneider/yasew.git
cd yasew/yasew-example
mvn test
```

To test against Android you need to setup [Appium](http://appium.io) and start an Appium server. You also need one physical or emulated device connected. Then simply execute the tests by setting `platform=android` in `yasew.properties`.

```bash
mvn test -Dyasew.properties=/absolute/path/to/your/yasew.properties
```

For now YASeW requires Java 8.

### Use in your own projects

Add the following Maven dependency to your `pom.xml`.

```xml
<dependency>
  <groupId>io.github.martinschneider</groupId>
  <artifactId>yasew-core</artifactId>
  <version>1.1-SNAPSHOT</version>
</dependency>
```

### Page objects, steps and feature files
There are three main ingredients for tests in YASeW:
* Page objects are a representation of a UI element (a page, a dialog, a screen etc.).
* Step definitions use page objects to define the actions of a test.  They form the building blocks to write
* feature files which represent the test cases.

Steps and page objects are designed to be highly re-usable.

Demo of a feature file:
```cucumber
Feature: Search and tags 

@web 
Scenario: Filter by tags 
	Given I am on the homepage 
	When I go to the tags page 
	And I filter for "selenium" 
	And I select the tag "selenium" 
	And I select the first question 
	Then the question is tagged with "selenium" 
	
@web @android 
Scenario: Use the search function 
	Given I am on the homepage 
	When I search for "selenium" 
	And I select the first question 
	Then the question is tagged with "selenium"
```

Demo of a step definition class:
```java
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
}
```

Demo of a page object:
```java
@Component
@Profile(Platform.WEB)
public class HomePage extends BasePage {
	
	private QuestionsPage questions;

	private TagsPage tags;

	public HomePage load() {
		open(configuration.getBaseUrl());
		return this;
	}

	public TagsPage navigateToTagsPage() {
		$("MENU_TAGS").click();
		return tags;
	}

	public QuestionsPage search(String query) {
		$("SEARCH_FIELD").sendKeys(query);
		$("SEARCH_BUTTON").should(Condition.appear).click();
		return questions;
	}
}
```

You can inject page objects in steps by declaring a private field:
```java
private HomePage home;
```

As long as the page object class extends `io.github.martinschneider.yasew.base.BasePage` YASeW (and [Spring](https://spring.io)) will take care of the rest. In the same way you can also use page objects inside other page objects.

### Configuration
All configuration goes in a file called `yasew.properties`.

```ini
# GENERAL settings
platform=web
pages.package=io.github.martinschneider.yasew.example.pages
steps.package=io.github.martinschneider.yasew.example.steps
features.directory=src/test/resources/features/carousell

# WEB settings
web.baseUrl=https://www.stackoverflow.com
web.browser=chrome

# MOBILE settings
mobile.appiumUrl=http://127.0.0.1:4723/wd/hub

# ANDROID settings
android.deviceName=0123456789ABCDEF
android.appPath=/path/to/apkfile.apk
android.appPackage=com.stackexchange.stackoverflow
android.appActivity=com.stackexchange.stackoverflow.MainActivity

# IOS settings
ios.appPath=/path/to/ipafile.ipa # real device
ios.appPath=/path/to/ipafile.app # simulator
ios.deviceName=iPhone 7
```

You can specify the location of `yasew.properties` on start-up by providing it as a system property: `-DyasewProperties=/path/to/yasew.properties`. If no path is specified it will be loaded from the classpath.

### Test runner
YASeW uses [JUnit](https://junit.org) to run the tests. All you need to do is add an empty class which extends `io.github.martinschneider.yasew.YasewTest`:

```java
public class TestRunner extends YasewTest{
}
```

Alternatively, you can also use the JUnit test runner directly:
```java
@RunWith(YasewRunner.class)
public class SomeTestClass {
}
```

The feature files and steps are automatically picked up from the locations provided in `yasew.properties`.

### Locators
Elements can be identified by a unique `ID`, a `CSS` or an `XPATH` expression. `AccesibilityId` and `UIAutomator` are supported as well. Each element has a unique key (e.g. `SEARCH_FIELD`) which is mapped to its corresponding locator expression in a .`properties` file. There is one general `.properties` file for every page object. If the locators differ between platforms there are additional files for each platform. The files have the same name as the corresponding JAVA class and are put under the same folder.
For example the page object for the homepage is `demoproject.pages.HomePage` (under `/src/main/java`). Then the corresponding locators are expected in `/demoproject/pages/HomePage.properties` (under `/src/main/resources`). The platform-specific ones go in sub-folders, e.g. `/src/main/resources/demoproject/pages/web/HomePage.properties`:

Example of a locator properties file:
```ini
MENU_TAGS=ID|nav-tags
SEARCH_FIELD=input[name=q]
SEARCH_BUTTON=.iconSearch
```

The correct locator will be automatically resolved for the current platform. Taking the above example, the search field can be accessed in the `HomePage` page object by calling `$("SEARCH_BUTTON")`. This will return an instance of `com.codeborne.selenide.SelenideElement`. See the [Selenide quick start](http://selenide.org/quick-start.html) to learn about all the cool ways you can interact with it. Two caveats to take note of:

1. It is not possible to directly use elements in step definitions (only in page objects). This is by design as UI elements are meant to be encapsulated by the page objects.
2. While we wrap Selenide's `$` method for the locator handling the methods you can call on the returned `SelenideElement` instances remains the same. 

The type of a locator is specified by adding a prefix (followed by a `|`):
```
ID|someId
XPATH|someXPathExpression
CSS|someCSSSelector
ACCESSIBILITY_ID|someAccesibilityId # for iOS tests with XCUITest
UIAUTOMATOR|someUiAutomator # for Android tests with UiAutomator
```

If omitted the default is `CSS`.

#### Placeholders
Locators can include placeholders which will be replaced by variables passed to the `$` method. For example:

```ini
POST_TAG=XPATH|//A[contains(@class,'post-tag') and contains(text(),'%s')]
```

Calling `$("POST_TAG", "selenium")` will return an element matching the following Xpath expression: `//A[contains(@class,'post-tag') and contains(text(),'selenium')`.

### Used frameworks

YASeW makes use of a variety of frameworks to make writing and executing tests as transparent and simple as possible.

* [Selenium](https://www.seleniumhq.org), the main test framework used by YASeW
* [Appium](http://appium.io), an extension of Selenium for mobile testing
* [Cucumber](https://cucumber.io), the BDD framework
* [JUnit](https://junit.org), the unit testing framework (mostly used as the runner for the tests)
* [Selenide](http://selenide.org), a convenience mapper around Selenium
* [AssertJ](http://joel-costigliola.github.io/assertj), fluent assertions for unit tests
* [OpenCV](https://opencv.org), used for image comparison
* [Spring](https://spring.io), IoC container for some added "magic" behind the scenes

### Presentations ###

This framework started as a PoC for the 2nd Singapore Appium Meet-up. Videos of the presentation can be found below.

#### Part 1 ####
[![Part 1](http://img.youtube.com/vi/OyAMnBEbT20/0.jpg)](http://www.youtube.com/watch?v=OyAMnBEbT20)

#### Part 2 ####
[![Part 2](http://img.youtube.com/vi/maJkvP_qk4A/0.jpg)](http://www.youtube.com/watch?v=maJkvP_qk4A)

### Contact and support

[Martin Schneider - mart.schneider@gmail.com](mailto:mart.schneider@gmail.com)
