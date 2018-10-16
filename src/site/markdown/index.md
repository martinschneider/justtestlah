# JustTestLah! 🇸🇬

[![Build status](https://travis-ci.org/martinschneider/justtestlah.svg?branch=master)](https://travis-ci.org/martinschneider/justtestlah) [![Maven Central](https://img.shields.io/maven-central/v/io.github.martinschneider/justtestlah-core.svg)](http://mvnrepository.com/artifact/io.github.martinschneider/justtestlah-core)

JustTestLah! is a JAVA test framework targeting projects that support multiple platforms, in particular Web, Android and iOS. It follows a [BDD](https://martinfowler.com/bliki/GivenWhenThen.html) approach and allows testing against all platforms using the same feature files. JustTestLah's main aim is to make the configuration as easy and the test code as simple and readable as possible.

## Getting started
Pull the repo and run the example. It includes automated tests for [Stack Overflow](https://stackoverflow.com) and [Carousell](https://www.carousell.com).

```bash
git clone https://github.com/martinschneider/justtestlah.git
mvn test -Dtest=TestRunner
```

The default platform is `web`. To test one of the mobile apps you need to setup [Appium](https://appium.io) and start an Appium server. You also need one physical or emulated device connected. Then simply execute the tests by setting `platform=android` or `platform=ios` in `justtestlah.properties`. Please note that the Stackoverflow demo is only available for `web` and `android` (upvote [this question](https://meta.stackoverflow.com/questions/365573/is-there-a-version-of-the-stack-overflow-app-for-the-ios-simulator) to help change this). For the Carousell demo you need to have a [Carousell](https://www.carousell.com) account.

```bash
mvn test -Dtest=TestRunner -Djusttestlah.properties=/absolute/path/to/your/justtestlah.properties
```

## Use in your own projects

Add the following Maven dependency to your `pom.xml`.

```xml
<dependency>
  <groupId>io.github.martinschneider</groupId>
  <artifactId>justtestlah-core</artifactId>
  <version>1.2</version>
  <!-- You can also use the latest snapshot version instead -->
  <!-- <version>1.3-SNAPSHOT</version> -->
</dependency>
```

## Page objects, steps and feature files
There are three main ingredients for tests in JustTestLah!:

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
public class HomePage extends BasePage<HomePage> {

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
    $("SEARCH_BUTTON").should(appear).click();
    return questions;
  }
}
```

You can inject page objects in steps by declaring a private field:
```java
private HomePage home;
```

As long as the page object class extends `io.github.martinschneider.justtestlah.base.BasePage` JustTestLah! (and [Spring](https://spring.io)) will take care of the rest. In the same way you can also use page objects inside other page objects.

## Configuration
All configuration goes in a file called `justtestlah.properties`.

```ini
# GENERAL settings
platform=web
pages.package=io.github.martinschneider.justtestlah.examples.stackoverflow.pages
steps.package=io.github.martinschneider.justtestlah.examples.stackoverflow.steps
features.directory=src/test/resources/features/stackoverflow
cucumber.report.directory=target/report/cucumber

# optional
galen.report.directory=target/report/galen
galen.enabled=false
eyes.enabled=false
opencv.enabled=false
eyes.apiKey=
cloudprovider=local


# WEB settings
web.baseUrl=https://www.stackoverflow.com
web.browser=chrome
web.headless=true


# MOBILE settings
mobile.appiumUrl=http://127.0.0.1:4723/wd/hub


# ANDROID settings
android.appPackage=com.stackexchange.stackoverflow
android.appActivity=com.stackexchange.stackoverflow.MainActivity
android.appPath=/Users/martinschneider/stackoverflow.apk
android.deviceName=Google Nexus 6


# IOS settings
ios.appPath=
ios.deviceName=iPhone 6


# BROWSERSTACK settings (requires cloudprovider=browserstack)
browserstack.debug=true
browserstack.accessKey=
browserstack.username=
```

You can specify the location of `justtestlah.properties` on start-up by providing it as a system property: `-DjusttestlahProperties=/path/to/justtestlah.properties`. If no path is specified it will be loaded from the classpath.

## Test runner
JustTestLah! uses [JUnit](https://junit.org) to run the tests. All you need to do is add an empty class which extends `io.github.martinschneider.justtestlah.JustTestLahTest`:

```java
public class TestRunner extends JustTestLahTest {}
```

Alternatively, you can also use the JUnit test runner directly:
```java
@RunWith(JustTestLahRunner.class)
public class SomeTestClass {}
```

The feature files and steps are automatically picked up from the locations provided in `justtestlah.properties`.

## Locators
Elements can be identified by a unique `id`, a `css` or an `xpath` expression. `AccesibilityId` (for iOS) and `UIAutomator` (for Android) are supported as well. Each element has a unique key (e.g. `SEARCH_FIELD`) which is mapped to its corresponding locator expression in a .`yaml` file.
For example, let's say the page object for the home page is `demoproject.pages.HomePage` (under `/src/main/java`). Then the corresponding locators are expected in `/demoproject/pages/HomePage.yaml` (under `/src/main/resources`).

Example of a locator YAML file:
```yaml
LOGIN_BUTTON:
  web:
    type: xpath
    value: "//BUTTON//SPAN[text()='Log in']"
  ios:
    type: accesibilityId
    value: login_page_login_button
  android:
    type: id
    value: com.thecarousell.Carousell:id/login_page_login_button
```

The correct locator will be automatically resolved for the current platform. Taking the above example, the search field can be accessed in the `HomePage` page object by calling `$("SEARCH_BUTTON")`. This will return an instance of `com.codeborne.selenide.SelenideElement`. See the [Selenide quick start](https://selenide.org/quick-start.html) to learn about all the cool ways you can interact with it. Two caveats to take note of:

1. It is not possible to directly use elements in step definitions (only in page objects). This is by design as UI elements are meant to be encapsulated in the page objects.
2. While we wrap Selenide's `$` method for the locator handling the methods you can call on the returned `SelenideElement` instances remains the same. 

If omitted the default type of locators is `css`.

### Placeholders
Locators can include placeholders which will be replaced by variables passed to the `$` method. For example:

```yaml
POST_TAG:
  web:
    type: xpath
    value: "//A[contains(@class,'post-tag') and contains(text(),'%s')]"
```

Calling `$("POST_TAG", "selenium")` will return an element matching the following Xpath expression: `//A[contains(@class,'post-tag') and contains(text(),'selenium')`.


## Galen
JustTestLah! includes a proof-of-concept integration of the [Galen framework](https://galenframework.com). It can be enabled by setting `galen.enabled=true` in `justtestlah.properties`.

Similar to properties-file holding the locator information, there is an (optional) spec file for each page object (in the same package as the Java class under src/main/resources).

Checks can be triggered by calling `checkLayout()` on any page object class. An HTML report is generated in the directory defined in `galen.report.directory` in `justtestlah.properties` (the default is `target/galen-reports/`).

```
@objects
  username_field  id  com.thecarousell.Carousell:id/login_page_username_text_field
  password_field  id  com.thecarousell.Carousell:id/login_page_password_text_field
  login_button    id  com.thecarousell.Carousell:id/login_page_login_button

= Login =

  username_field:
      above password_field
      aligned vertically all password_field
      width 100 % of password_field/width
      width 100 % of login_button/width
	
  password_field:
      below username_field
      aligned vertically all username_field
      width 100 % of username_field/width
      width 100 % of login_button/width
        
  login_button:
      below password_field
      below username_field
      width 100 % of username_field/width
      width 100 % of password_field/width
      text is "Log In" 
```

See the [Galen documentation](https://galenframework.com/docs/reference-galen-spec-language-guide) for more examples.

## Applitools

There is a proof-of-concept integration of [Applitools](https://applitools.com). It can be enabled by setting `eyes.enabled=true` in `justtestlah.properties`. In addition a valid API key must be specified: `eyes.apiKey=...`.

Checks can then be triggered by calling `checkWindow()` on any page object class (the initial run will create baseline images). Please note that Applitools is a paid service.

## Browserstack

You can run tests against [BrowserStack](https://www.browserstack.com) by adding the following configuration in `justtestlah.properties`:

```
cloudprovider=browserstack
browserstack.accessKey=
browserstack.username=
```

Please note that BrowserStack is a paid service.

## Used frameworks

JustTestLah! makes use of a variety of frameworks to make writing and executing tests as transparent and simple as possible.

* [Selenium](https://www.seleniumhq.org), the main test framework used by JustTestLah!
* [Appium](https://appium.io), an extension of Selenium for native mobile app testing
* [Cucumber](https://cucumber.io), the BDD framework
* [JUnit](https://junit.org), the unit testing framework (mostly used as the runner for the tests)
* [Selenide](https://selenide.org), a convenience mapper around Selenium
* [AssertJ](https://joel-costigliola.github.io/assertj), fluent assertions for unit tests
* [OpenCV](https://opencv.org), used for image comparison
* [Galen](https://galenframework.com), used for layout based testing
* [Applitools](https://applitools.com), used for visual regression testing
* [BrowserStack](https://www.browserstack.com), cloud provider for automated tests
* [Spring](https://spring.io), IoC container for some added "magic" behind the scenes

## Presentations

This framework started as a PoC for the 2nd Singapore Appium Meet-up. Videos of the presentation can be found below.

[Part 1](https://www.youtube.com/watch?v=OyAMnBEbT20)
[Part 2](https://www.youtube.com/watch?v=maJkvP_qk4A)

## Contact and support

[Martin Schneider - mart.schneider@gmail.com](mailto:mart.schneider@gmail.com)

[![Buy me a coffee](https://www.buymeacoffee.com/assets/img/custom_images/yellow_img.png)](https://www.buymeacoffee.com/mschneider)
