> 🇸🇬 lah ([Singlish](https://en.wikipedia.org/wiki/Singlish)) - Placed at the end of a phrase or sentence either for emphasis or reassurance.

[![Build status](https://travis-ci.com/martinschneider/justtestlah.svg?branch=master)](https://travis-ci.com/martinschneider/justtestlah)
[![Coverage Status](https://codecov.io/gh/martinschneider/justtestlah/branch/master/graph/badge.svg)](https://codecov.io/gh/martinschneider/justtestlah)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/d6be39f81b7341769e8c67ed26b29b19)](https://www.codacy.com/manual/martinschneider/justtestlah?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=martinschneider/justtestlah&amp;utm_campaign=Badge_Grade)

JustTestLah! is a JAVA test framework. It follows a [BDD](https://martinfowler.com/bliki/GivenWhenThen.html) approach and allows testing on different platforms (Android, iOS and Web) using the same test scenarios. JustTestLah's main aim is to make the configuration as easy and the test code as simple, readable and maintainable as possible.

## Getting started
Pull the repo and run the demo (a set of simple "tests" for Stackoverflow):

```bash
git clone https://github.com/martinschneider/justtestlah.git
mvn -pl justtestlah-demos test
```

This runs the JustTestLah! demo using Chrome in [headless mode](https://developers.google.com/web/updates/2017/04/headless-chrome). Set `web.headless=false` in `justtestlah-demos/src/test/resources` to change this.

### justtestlah.properties
The file `justtestlah.properties` holds all parameters required for a test run and is the only source of configuration which needs to be specified. It will be loaded from the classpath by default, but it is recommended to explicitly pass its path as a system property:

```bash
mvn test -DjtlProps=/absolute/path/to/your/testabc.properties
```

This way, you can easily maintain different configurations for different test setups.

The most important properties are:

```ini
# Platform to test on (Android, iOS, Web)
platform=

# The path to the Cucumber feature files
features.directory=

# Java package containing the Cucumber steps
steps.package=

# Java package containing the Page objects
pages.package=
```

Each run will execute tests for one platform only. Let's see how we can run the Android demo next.

### Android demo

To test a mobile app you need to setup [Appium](https://appium.io) and [start an Appium server](http://appium.io/docs/en/about-appium/getting-started). Make sure that there is at least one physical or virtual (Android emulator or iPhone simulator) device connected. Then simply execute the tests by setting `platform=android` in your JustTestLah! properties file. This is the only difference to the configuration for Web.

### iOS demo

There is currently no public demo for iOS available. This is mostly because app packages (builds for the iPhone simulator) for any interesting real-world application are not readily available and [ipa builds need to be resigned to play nicely with Appium](http://appium.io/docs/en/drivers/ios-xcuitest-real-devices). If you want to contribute a demo, [please conatct me](mart.schneider@gmail.com).

That said, using JustTestLah! can be (and has been) used to automate iOS apps. 

### Available demos
There are a couple of demos available under the `justtestlah-demos` module. The default one uses [Stackoverflow](https://stackoverflow.com) and comes in flavours for `web` and `android` (upvote [this question](https://meta.stackoverflow.com/questions/365573/is-there-a-version-of-the-stack-overflow-app-for-the-ios-simulator) to help us get access to an iOS version.

Which tests get executed depends on the `features.dierctory` property:

```ini
# The path to the Cucumber feature files
features.directory=
```

On top of that, we need to specify where the corresponding steps classes and page objects can be found:

```ini
# Java package containing the Cucumber steps
steps.package=

# Java package containing the Page objects
pages.package=
```

All demos are rather simple proofs of concept, please [create a pull request](https://github.com/martinschneider/JustTestLah/pulls) if you want to contribute more.

## Use JustTestLah! in your own projects

It's simple!

### Option 1: Using Maven archetype

A fast way to get a working template project is using the JustTestLah! [Maven archetype](https://maven.apache.org/guides/introduction/introduction-to-archetypes.html):
```bash
mvn archetype:generate -DarchetypeGroupId=qa.justtestlah -DarchetypeArtifactId=justtestlah-quickstart
```

### Option 2: Manual setup using Maven
Add the following to your `pom.xml`:

```xml
<dependency>
  <groupId>qa.justtestlah</groupId>
  <artifactId>justtestlah-core</artifactId>
  <version>1.7</version>
</dependency>
```

### Option 3: Manual setup using Gradle
Add the following to your `build.gradle`:

```yaml
compile group: 'qa.justtestlah', name: 'justtestlah-core', version: '1.7'
```

### Option 4: Manual setup
Add [`justtestlah-core-1.7.jar`](https://repo1.maven.org/maven2/qa/justtestlah/justtestlah-core/1.7/justtestlah-core-1.7.jar) to your classpath.

## Page objects, steps and feature files
There are three main ingredients for tests in JustTestLah!:

- Page objects are a representation of a UI element (a page, a dialog, a screen etc.).
- Step definitions use page objects to define the actions of a test.  They form the building blocks to write
- feature files which represent the test scenarios.

Steps and page objects are designed to be highly re-usable.

Demo of a feature file:
```gherkin
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

  @Given("I am on the homepage")
  public void homepage() {
    home.load();
  }

  @When("I go to the tags page")
  public void goToTags() {
    home.navigateToTagsPage();
  }

  @When("I search for {string}")
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

As long as the page object class extends `qa.justtestlah.base.BasePage` JustTestLah! (and [Spring](https://spring.io)) will take care of the rest. In the same way you can also use page objects inside other page objects.

### Platform-(in)dependent page objects

[Spring profiles](https://www.baeldung.com/spring-profiles) are used to identify for which platforms (Android, iOS, Web) a page object shoulde be used. Simply annotate the page object class with `@Profile` and pass an array of platforms as its argument:

```java
@Component
@Profile({ANDROID, WEB})
public class LoginPage extends BasePage<LoginPage>
```

Ideally, the same Java object can represent a page for all platforms. This is the case when the only differences are the UI locators (as their keys are platform-independent; their platform-dependent values are only resolved at runtime).

When the above approach is not sufficient, you can use different page object classes for different platforms. In this case, it can be useful to have a base class containing common methods and subclass it for any platform-specific changes.

Make sure that there is exactly one page object class for each page/platform combination. Otherwise, Spring will throw an error during start-up.

## Configuration
As mentioned before, all configuration goes in a file called `justtestlah.properties`. Its path can be passed using the `jtlProps` system property:

```bash
-DjtlProps=/path/to/justtestlah.properties
```

If no path is specified, configuration will be loaded from the classpath (in this case the file must be named `justtestlah.properties`).

The following is a complete list of available properties. You don't need to specify all as most of them are optional.

```ini
# GENERAL settings
platform=web
pages.package=qa.justtestlah.examples.stackoverflow.pages
steps.package=qa.justtestlah.examples.stackoverflow.steps
features.directory=src/test/resources/features/stackoverflow
cucumber.report.directory=target/report/cucumber

# Galen
galen.enabled=false
galen.inject.locators
galen.report.directory=target/report/galen

# Applitools
eyes.enabled=false
eyes.apiKey=

# Template matching
opencv.enabled=false

# OCR
tesseract.datapath=

# Cloudprovider (`aws` or `browserstack`, the default is `local`)
cloudprovider=local

# WEB settings
web.baseUrl=https://www.stackoverflow.com
web.browser=chrome
web.headless=true

# MOBILE settings
mobile.appiumUrl=http://127.0.0.1:4723/wd/hub
mobile.deviceOrientation=portrait

# ANDROID settings
android.appPackage=com.stackexchange.stackoverflow
android.appActivity=com.stackexchange.stackoverflow.MainActivity
android.appPath=/Users/martinschneider/stackoverflow.apk
android.deviceName=Google Nexus 6

# IOS settings
ios.appPath=
ios.deviceName=iPhone 6

# BROWSERSTACK settings (requires `cloudprovider=browserstack` and `justtestlah-browserstack` on the classpath)

# Browserstack username
browserstack.username=
# Browserstack access key
browserstack.accessKey=

# Optional settings, see https://www.browserstack.com/automate/capabilities
browserstack.project=
browserstack.build=
browserstack.debug=true
browserstack.appiumLogs=true
browserstack.video=true
browserstack.geoLocation=SG
browserstack.timezone=SG
browserstack.appium_version=1.8.0
browserstack.acceptSslCerts=true

# AWS DEVICEFARM settings (requires `cloudprovider=aws` and `justtestlah-awsdevicefarm` on the classpath)
# The arn of your AWS Devicefarm project (mandatory)
aws.projectArn=

# App package to use. If this value is empty it will be created and uploaded to AWS Devicefarm before the test execution
aws.appPackageArn=

# Test package to use. If this value is empty it will be created and uploaded to AWS Devicefarm before the test execution
aws.testPackageArn=

# Optional extra data
aws.extraDataArn=

# Fully-qualified path to the justtestlah-demos project (required to build the test package)
aws.demo.path=/Users/martinschneider/git/justtestlah/justtestlah-demos

# Name for the test package (must match <finalName> in the justtestlah-demos pom.xml)
aws.testpackage.name=justtestlah-awsdevicefarm

# Device filters (optional)
aws.minOsVersion=9.0
aws.maxOsVersion=
aws.osVersion=
aws.model=
aws.manufacturer=
aws.formFactor=PHONE
aws.waitForDevice=true

# Device configuration (optional)
aws.deviceLatitude=
aws.deviceLongitude=
aws.bluetooth=
aws.gps=
aws.nfc=
aws.wifi=
# set this to true if you use device slots
aws.runUnmetered=false

# Additional AWS Devicefarm configuration
aws.accountsCleanup=
aws.appPackagesCleanup=
aws.jobTimeOut=
aws.skipAppResign=
```

## Test runner
JustTestLah! uses [JUnit](https://junit.org) to execute the tests. All you need to do is add an empty class which extends `qa.justtestlah.JustTestLahTest`:

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

Locators for different platforms "live" side by side in the same yaml file. Locators are grouped by page object rather than by platform.

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
1. While we wrap Selenide's `$` method for the locator handling the methods you can call on the returned `SelenideElement` instances remains the same.

If omitted the default type of locators is `css`.

### Placeholders
Locators can include both dynamic and static placeholders which will be replaced by variables passed to the `$` method.

#### Static placeholders
You can think of static placeholders as variables. They can be defined in a file called `placeholder.properties` in the root of the pages package (specified as `pages.package` in `justtestlah.properties`). This is the same folder the locator YAML files are placed in.

This file continues key/value pairs in the following format:

```ini
SOME_KEY=someValue
PACKAGE_NAME=com.stackexchange.stackoverflow
```

One use-case, as shown above, can be to define the Android package name as a variable and use it in all `id`-based locators like this:

```yaml
POST_TAG:
  android:
    type: id
    value: ${PACKAGE_NAME}:id/question_view_item_tags  
```

If you want to override static placeholders during runtime, you can pass an extra placeholder file by setting `locator.placeholders.file` to its absolute path in `justtestlah.properties`. In this case, for any placeholders which occur in both files (the one under `pages.package` and `locator.placeholders.file`), the latter one will override the former.

#### Dynamic placeholders

Sometimes, you might require a locator which depends on some dynamic values defined only at runtime. You can achieve this by putting `%s` as a placeholder in the locator and use the `$(String locatorKey, Object... params)` and `$$(String locatorKey, Object... params)` methods in `BasePage` to pass the String which should be inserted at its place.

Let's see an example:

```yaml
POST_TAG:
  web:
    type: xpath
    value: "//A[contains(@class,'post-tag') and contains(text(),'%s')]"
```

Calling `$("POST_TAG", "selenium")` will return an element matching the following Xpath expression: `//A[contains(@class,'post-tag') and contains(text(),'selenium')`.

## Test data handling
JustTestLah! supports loading test data from YAML files. Each test data entity is represented by a Java class (the model) and one or many YAML files which contain the actual test data. For example:

```java
@TestData
public class User {
  private String username;
  private String password;

  public User() {}

  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
```

```yaml
user:
  username: myUsername
  password: myPassword
```

Note, that the top level key in the YAML file must match the name of the corresponding test data class. This is specified as the value of its `@TestData` annotation. If this is ommited, the (lower camel case) name of the class will be used instead.

You can then load test data in your tests as easy as this:

```java
User defaultUser = testdata(User.class);
User validUser = testdata(User.class, "validUser");
User invalidUser = testdata(User.class, "userWithInvalidPassword");
```

The second parameter points to the name of the test entity which is the filename of the YAML file. If ommited, it defaults to `default`. In the above example, you would have three YAML files: `default.yaml`, `validUser.yaml` and `userWithInvalidPassword`.

There are three configuration values for this feature:
```testdata.enabled=
model.package=
testdata.filter=
```

Setting `testdata.enabled=true` enables the YAML test data resolution. The default is `false` so don't forget to set this if you use this feature in your projects.

`model.package` is mandatory and specifies the root package to scan for Java objects representing test entities (these classes need to be annotated with `@TestData`).

`testdata.filter` allows restricting the path to scan for test data YAML files. If left empty everything matching `**/testdata/**/*.y*ml` (under `src/test/resources`) will be considered.

Please note, that there is an additional check for the string `testdata` in the path of any testdata YAML files. Make sure that all testdata files are in a folder of that name or use `testdata` as a prefix or suffix in the filename.

## Test reports

After the run, [Cluecumber](https://github.com/trivago/cluecumber-report-plugin) test results will be available under `target/report/cucumber`.

## Cloud service integrations

JustTestLah! supports integrating with various cloud service provides. Some of them are in PoC state. Please feel free to contribute.

### Browserstack

You can run tests against [BrowserStack](https://www.browserstack.com) by adding the following configuration in `justtestlah.properties`:

```properties
cloudprovider=browserstack

# Browserstack username
browserstack.username=
# Browserstack access key
browserstack.accessKey=

# Optional settings, see https://www.browserstack.com/automate/capabilities
browserstack.debug=true
browserstack.appiumLogs=true
browserstack.video=true
browserstack.geoLocation=SG
browserstack.networkProfile=
browserstack.customNetwork=
browserstack.timezone=SG
browserstack.appium_version=1.8.0
```

Make sure `justtestlah-browserstack.jar` is on your classpath:

```xml
<dependency>
  <groupId>qa.justtestlah</groupId>
  <artifactId>justtestlah-browserstack</artifactId>
  <version>${project.version}</version>
</dependency>
```

Please note that BrowserStack is a paid service.

### AWS Devicefarm

You can run tests against [AWS Devicefarm](https://us-west-2.console.aws.amazon.com/devicefarm/) by adding the following configuration in `justtestlah.properties`:

```properties
cloudprovider=aws

# The arn of your AWS Devicefarm project (mandatory)
aws.projectArn=

# App package to use. If this value is empty it will be created and uploaded to AWS Devicefarm before the test execution
aws.appPackageArn=

# Test package to use. If this value is empty it will be created and uploaded to AWS Devicefarm before the test execution
#aws.testPackageArn=

# Optional extra data
aws.extraDataArn=

# Fully-qualified path to the justtestlah-demos project (required to build the test package)
aws.demo.path=

# Name for the test package (must match <finalName> in the justtestlah-demos pom.xml)
aws.testpackage.name=justtestlah-awsdevicefarm

# Device filters (optional)
aws.minOsVersion=9.0
aws.maxOsVersion=
aws.osVersion=
aws.model=
aws.manufacturer=
aws.formFactor=PHONE
aws.waitForDevice=true

# Optional device configuration
aws.deviceLatitude=
aws.deviceLongitude
aws.bluetooth=
aws.gps=
aws.nfc=
aws.wifi=
# set this to true if you use device slots
aws.runUnmetered=false

# Additional AWS Devicefarm configuration
aws.accountsCleanup=
aws.appPackagesCleanup=
aws.jobTimeOut=
aws.skipAppResign=
```

Make sure `justtestlah-awsdevicefarm.jar` is on your classpath:

```xml
<dependency>
  <groupId>qa.justtestlah</groupId>
  <artifactId>justtestlah-awsdevicefarm</artifactId>
  <version>${project.version}</version>
</dependency>
```

You can refer to [this article](https://medium.com/@mart.schneider/mobile-test-automation-using-aws-device-farm-6bcf825fa27d) for a more detailled description of how to tweak AWS Devicefarm.

Please note that AWS Devicefarm is a paid service.

## Template matching
Make sure `justtestlah-visual.jar` is on your classpath:

```xml
<dependency>
  <groupId>qa.justtestlah</groupId>
  <artifactId>justtestlah-visual</artifactId>
  <version>${project.version}</version>
</dependency>
```

JustTestLah! allows locating elements using a template image:

```java
boolean isImagePresent = homePage.hasImage("questionIcon.png");

Match image = homePage.findImage("questionIcon.png");
```

The images are expected under `/src/test/resources/images`.

The `Match` object contains the x and y coordinate of the matched image (more precisely, the center of the rectangle representing the match). These can be used to interact with an element located this way. For example, we can tap on an element like this:

```java
new TouchAction((PerformsTouchActions) WebDriverRunner.getWebDriver())
.tap(PointOption.point(questionIcon.getX(), questionIcon.getY()))
.perform();
```

Future versions of JustTestLah! will include wrappers to perform these actions more conveniently.

The `TemplateMatcher` is scale-invariant (to some extent). The algorithm used to achieve this scales the target image (a screenshot of the device) up and down until either a match is found or a minimum (320) or maximum (3200) image width is reached.

Note, that the closer the size of the template matches the size of the image on the screen the faster and more accurate the matching will be.

### Matching threshold
Both the `hasImage` and `findImage` method take an optional `threshold` parameter which can be used to define the accuracy of a match. The possible values range from 0 (no match) to 1 (pixel-perfect match). The default is `0.9`.

### Client and server-mode matching
There are two modes to use template matching which can be configured in `justtestlah.properties`:

`opencv.mode=client` performs the image matching on the client (i.e. the machine running the test code). It requires [OpenCV](https://github.com/openpnp/opencv) which is imported as a Maven dependency.

`opencv.mode=server` utilises the [image matching feature of Appium](https://appium.readthedocs.io/en/latest/en/writing-running-appium/image-comparison). This requires OpenCV to be installed on the machine which runs the Appium server.

Note, that not all cloud providers support this.

## OCR

JustTestLah! integrates [Tesseract](https://github.com/tesseract-ocr/tesseract) to perform [Optical character recognition](https://en.wikipedia.org/wiki/Optical_character_recognition).

This requires `justtestlah-visual.jar` on the classpath:

```xml
<dependency>
  <groupId>qa.justtestlah</groupId>
  <artifactId>justtestlah-visual</artifactId>
  <version>${project.version}</version>
</dependency>
```

JustTestLah! uses [Tess4J](http://tess4j.sourceforge.net/), a Java wrapper for Tesseract. You still need to [install native binaries on your machine](https://github.com/tesseract-ocr/tesseract/wiki#installation) and set the [tesseract datapath](https://github.com/tesseract-ocr/tesseract/wiki/Data-Files) in the `justtestlah.properties`:

On a Mac, this might look something like this:

```ini
tesseract.datapath=/usr/local/Cellar/tesseract/4.1.1/share/tessdata
``` 

To use this feature, simply autowire an instance of the `OCR` class and pass a `WebElement` to its `getText` method. If you don't pass an argument, OCR will be performed on a screenshot of the entire viewport.

Here is a demo where we get the text of the main logo on the Google search page:

```java
@Component
@Profile(WEB)
@ScreenIdentifier("SEARCH_FIELD")
public class GooglePage extends BasePage<GooglePage> {

  @Autowired private OCR ocr;

  public String getLogoText() {
    return ocr.getText($("LOGO"));
  }
}

```

In the step class, we can then perform a check like this:

```java
assertThat(googlePage.getLogoText()).isEqualTo("Google");
``` 

## Applitools

Make sure `justtestlah-applitools.jar` is on your classpath:

```xml
<dependency>
  <groupId>qa.justtestlah</groupId>
  <artifactId>justtestlah-applitools</artifactId>
  <version>${project.version}</version>
</dependency>
```

There is a proof-of-concept integration of [Applitools](https://applitools.com). It can be enabled by setting `eyes.enabled=true` in `justtestlah.properties`. In addition a valid API key must be specified: `eyes.apiKey=...`.

Checks can then be triggered by calling `checkWindow()` on any page object class (the initial run will create baseline images).

Please note that Applitools is a paid service.

## Galen

Make sure `justtestlah-galen` is on your classpath:

```xml
<dependency>
  <groupId>qa.justtestlah</groupId>
  <artifactId>justtestlah-galen</artifactId>
  <version>${project.version}</version>
</dependency>
```

JustTestLah! includes a proof-of-concept integration of the [Galen framework](https://galenframework.com). It can be enabled by setting `galen.enabled=true` in `justtestlah.properties`.

Similar to properties-file holding the locator information, there is an (optional) spec file for each page object (in the same package as the Java class under src/main/resources).

Checks can be triggered by calling `checkLayout()` on any page object class. An HTML report is generated in the directory defined in `galen.report.directory` in `justtestlah.properties` (the default is `target/galen-reports/`).

```yaml
= Login =

  SEARCH_FIELD:
    below LOGO
    centered horizontally inside viewport
    visible

  LOGO:
    above SEARCH_FIELD
    centered horizontally inside viewport
    width < 100% of SEARCH_FIELD/width
    visible 
```

Note, that you do not need to specify the @objects section in the Galen spec. This will be auto-generated during runtime based on the page object YAML file. You can refer to any UI element using its key. 

See the [Galen documentation](https://galenframework.com/docs/reference-galen-spec-language-guide) for more examples.

## Used libraries

JustTestLah! makes use of a variety of frameworks to make writing and executing tests as simple and enjoyable as possible.

- [Selenium](https://www.seleniumhq.org), the main test framework used by JustTestLah!
- [Appium](https://appium.io), an extension of Selenium for native mobile app testing
- [Cucumber](https://cucumber.io), the BDD framework
- [JUnit](https://junit.org), the unit testing framework (mostly used as the runner for the tests)
- [Selenide](https://selenide.org), a convenience wrapper around Selenium
- [AssertJ](https://joel-costigliola.github.io/assertj), fluent assertions for unit tests
- [OpenCV](https://opencv.org), used for image comparison
- [Spring](https://spring.io), IoC container for some added "magic" behind the scenes
- [Galen](http://galenframework.com), used for layout based testing
- [Tesseract](https://github.com/tesseract-ocr/tesseract), used for Optical Charatcer Recognition (OCR)
- [Applitools](https://applitools.com), used for visual regression testing

## Known issues & limitations

- JustTestLah! requires Java 10 or higher (and has been tested on Java 10, 11, 12, 13 and 14). Java 9 support has been dropped because of [JDK-8193802](https://bugs.java.com/bugdatabase/view_bug.do?bug_id=8193802) which isn't fixed on Java below 10.
- The OpenCV integration (used for client-side template matching) [doesn't work with Java 12 and above yet](https://github.com/openpnp/opencv/issues/44).
- The mobile version of the Galen integration has only been tested against Appium 1.7. Please feel free to contribute an update for this feature.

## Contact and support

Please let me know about any feedback, questions or ideas for improvement.

[Martin Schneider - mart.schneider@gmail.com](mailto:mart.schneider@gmail.com)

[![Buy me a coffee](https://www.buymeacoffee.com/assets/img/custom_images/yellow_img.png)](https://www.buymeacoffee.com/mschneider)
