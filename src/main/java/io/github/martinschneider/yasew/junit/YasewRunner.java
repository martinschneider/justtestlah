package io.github.martinschneider.yasew.junit;

import cucumber.api.event.TestRunFinished;
import cucumber.api.junit.Cucumber;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.RuntimeOptionsFactory;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.junit.Assertions;
import cucumber.runtime.junit.FeatureRunner;
import cucumber.runtime.junit.JUnitOptions;
import cucumber.runtime.junit.JUnitReporter;
import cucumber.runtime.model.CucumberFeature;
import io.github.martinschneider.yasew.configuration.Platform;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import nu.pattern.OpenCV;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.opencv.core.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Custom JUnit runner to dynamically set cucumber.̰options.
 * Based on {@link Cucumber}. */
public class YasewRunner extends ParentRunner<FeatureRunner> {

  @SuppressWarnings("squid:S00116Field")
  private static final Logger LOG = LoggerFactory.getLogger(YasewRunner.class);

  private final JUnitReporter reporter;
  private final List<FeatureRunner> children = new ArrayList<>();
  private final Runtime runtime;
  private Properties props;

  private static final String STEPS_PACKAGE_KEY = "steps.package";
  private static final String PLATFORM_KEY = "platform";
  private static final String CUCUMBER_OPTIONS_KEY = "cucumber.options";
  private static final String FEATURES_DIRECTORY_KEY = "features.directory";
  private static final String DEFAULT_YASEW_PROPERTIES = "yasew.properties";
  private static final String YASEW_LOCATION_KEY = "yasew.properties";
  private static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";
  private static final String OPENCV_ENABLED_KEY = "opencv.enabled";
  private static final String CUCUMBER_REPORT_DIRECTORY_KEY = "cucumber.report.directory";
  private static final String DEFAULT_CUCUMBER_REPORT_DIRECTORY = "target/report/cucumber";
  private static final String DEFAULT_PLATFORM = "web";

  /**
   * Constructs a new {@link YasewRunner}.
   * 
   * @param clazz test class
   * @throws InitializationError {@link InitializationError}
   * @throws IOException {@link IOException}
   */
  public YasewRunner(Class<?> clazz) throws InitializationError, IOException {
    super(clazz);

    // Initialize Spring profiles and settings
    init();

    // load OpenCV library
    if (Boolean.parseBoolean(getProperty(OPENCV_ENABLED_KEY, "false"))) { // load the opencv library
      OpenCV.loadShared();
      OpenCV.loadLocally();
      System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    String cucumberOptions =
        "--tags @"
            + getProperty(PLATFORM_KEY, DEFAULT_PLATFORM)
            + " --glue io.github.martinschneider.yasew.steps --glue "
            + getProperty(STEPS_PACKAGE_KEY)
            + " --plugin pretty --plugin html:report --plugin json:"
            + getProperty(CUCUMBER_REPORT_DIRECTORY_KEY, DEFAULT_CUCUMBER_REPORT_DIRECTORY)
            + "/cucumber.json"
            + " "
            + getProperty(FEATURES_DIRECTORY_KEY);
    LOG.info("Setting cucumber options ({}) to {}", CUCUMBER_OPTIONS_KEY, cucumberOptions);
    System.setProperty(CUCUMBER_OPTIONS_KEY, cucumberOptions);
    ClassLoader classLoader = clazz.getClassLoader();
    Assertions.assertNoCucumberAnnotatedMethods(clazz);

    RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(clazz);
    RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();

    ResourceLoader resourceLoader = new MultiLoader(classLoader);
    runtime = createRuntime(resourceLoader, classLoader, runtimeOptions);
    final JUnitOptions junitOptions = new JUnitOptions(runtimeOptions.getJunitOptions());
    final List<CucumberFeature> cucumberFeatures =
        runtimeOptions.cucumberFeatures(resourceLoader, runtime.getEventBus());
    reporter =
        new JUnitReporter(runtime.getEventBus(), runtimeOptions.isStrict(), junitOptions);
    addChildren(cucumberFeatures);
  }

  protected Runtime createRuntime(
      ResourceLoader resourceLoader, ClassLoader classLoader, RuntimeOptions runtimeOptions)
      throws InitializationError, IOException {
    ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
    return new Runtime(resourceLoader, classFinder, classLoader, runtimeOptions);
  }

  @Override
  public List<FeatureRunner> getChildren() {
    return children;
  }

  @Override
  protected Description describeChild(FeatureRunner child) {
    return child.getDescription();
  }

  @Override
  protected void runChild(FeatureRunner child, RunNotifier notifier) {
    child.run(notifier);
  }

  @Override
  protected Statement childrenInvoker(RunNotifier notifier) {
    final Statement features = super.childrenInvoker(notifier);
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        features.evaluate();
        runtime.getEventBus().send(new TestRunFinished(runtime.getEventBus().getTime()));
        runtime.printSummary();
      }
    };
  }

  private void addChildren(List<CucumberFeature> cucumberFeatures) throws InitializationError {
    for (CucumberFeature cucumberFeature : cucumberFeatures) {
      FeatureRunner featureRunner = new FeatureRunner(cucumberFeature, runtime, reporter);
      if (!featureRunner.isEmpty()) {
        children.add(featureRunner);
      }
    }
  }

  private String getProperty(String key, String defaultValue) {
    initProperties();
    String value = props.getProperty(key);
    if (value != null && !value.isEmpty()) {
      LOG.debug("Reading property {} = {}", key, value);
      return value;
    }
    LOG.warn("Property {} not set in yasew.properties. Using default value: {}", key, defaultValue);
    return defaultValue;
  }

  private String getProperty(String key) {
    initProperties();
    String value = props.getProperty(key);
    if (value != null && !value.isEmpty()) {
      LOG.info("Reading property {} = {}", key, value);
      return value;
    }
    throw new RuntimeException("Mandatory property " + key + " not set in yasew.properties.");
  }

  private void initProperties() {
    if (props == null) {
      props = new Properties();
      loadProperties();
    }
  }

  private void loadProperties() {

    String propertiesLocation = System.getProperty(YASEW_LOCATION_KEY);
    try {
      if (propertiesLocation != null) {
        LOG.info("Loading Yasew properties from {}", propertiesLocation);
        props.load(new FileInputStream(propertiesLocation));
      } else {
        propertiesLocation = DEFAULT_YASEW_PROPERTIES;
        LOG.info("Loading Yasew properties from classpath ({})", propertiesLocation);
        props.load(YasewTest.class.getClassLoader().getResourceAsStream(propertiesLocation));
      }
    } catch (NullPointerException | IOException e) {
      LOG.warn("Error loading settings from {}", propertiesLocation);
    }
  }

  private void init() {
    // set the active Spring profile to the current platform
    String platform = getProperty(PLATFORM_KEY);
    if (platform == null || platform.isEmpty()) {
      LOG.info("No platform specified. Using default ({})", Platform.DEFAULT);
      platform = Platform.DEFAULT;
      System.setProperty(PLATFORM_KEY, platform);
    }
    String[] platforms = platform.split(",");
    if (platforms.length > 1) {
      throw new UnsupportedOperationException(
          "Please specify exactly one spring profile (ANDROID, IOS or WEB).");
    }
    platform = platforms[0].trim();
    String springProfiles = System.getProperty(SPRING_PROFILES_ACTIVE);
    if (springProfiles != null && !springProfiles.isEmpty()) {
      springProfiles += "," + platform;
    } else {
      springProfiles = platform;
    }
    LOG.info("Setting platform to {}", platform);
    System.setProperty(SPRING_PROFILES_ACTIVE, springProfiles);
  }
}
