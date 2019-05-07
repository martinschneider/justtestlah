package io.github.martinschneider.justtestlah.junit;

import cucumber.api.StepDefinitionReporter;
import cucumber.api.event.TestRunFinished;
import cucumber.api.event.TestRunStarted;
import cucumber.api.junit.Cucumber;
import cucumber.runner.EventBus;
import cucumber.runner.ThreadLocalRunnerSupplier;
import cucumber.runner.TimeService;
import cucumber.runner.TimeServiceEventBus;
import cucumber.runtime.BackendModuleBackendSupplier;
import cucumber.runtime.BackendSupplier;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.FeaturePathFeatureSupplier;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.filter.Filters;
import cucumber.runtime.formatter.PluginFactory;
import cucumber.runtime.formatter.Plugins;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.junit.Assertions;
import cucumber.runtime.junit.FeatureRunner;
import cucumber.runtime.junit.JUnitOptions;
import cucumber.runtime.model.CucumberFeature;
import cucumber.runtime.model.FeatureLoader;
import io.github.martinschneider.justtestlah.configuration.Platform;
import io.github.martinschneider.justtestlah.configuration.PropertiesHolder;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import nu.pattern.OpenCV;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.opencv.core.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/** Custom JUnit runner to dynamically set cucumber.̰options. Based on {@link Cucumber}. */
public class JustTestLahRunner extends ParentRunner<FeatureRunner> {

  public static final String AWS_JUNIT_GROUP_DESCRIPTION = "Test results";
  public static final String AWS_JUNIT_SUITE_DESCRIPTION = "AWS Devicefarm execution";

  private static final Logger LOG = LoggerFactory.getLogger(JustTestLahRunner.class);

  private final List<FeatureRunner> children = new ArrayList<FeatureRunner>();
  private EventBus eventBus = null;
  private ThreadLocalRunnerSupplier runnerSupplier = null;
  private Filters filters = null;
  private JUnitOptions junitOptions = null;
  private PropertiesHolder properties = new PropertiesHolder();

  private static final String STEPS_PACKAGE_KEY = "steps.package";
  private static final String CLOUD_PROVIDER = "cloudprovider";
  private static final String PLATFORM_KEY = "platform";
  private static final String TAGS_KEY = "tags";
  private static final String CUCUMBER_OPTIONS_KEY = "cucumber.options";
  private static final String FEATURES_DIRECTORY_KEY = "features.directory";
  private static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";
  private static final String OPENCV_MODE_KEY = "opencv.mode";
  private static final String CUCUMBER_REPORT_DIRECTORY_KEY = "cucumber.report.directory";
  private static final String JUSTTESTLAH_SPRING_CONTEXT_KEY = "justtestlah.use.springcontext";
  private static final String DEFAULT_CUCUMBER_REPORT_DIRECTORY = "target/report/cucumber";
  private static final String DEFAULT_PLATFORM = "web";
  private static final String DELIMITER = ",";

  private Runner awsRunner;

  /**
   * Constructs a new {@link JustTestLahRunner}.
   *
   * @param clazz test class
   * @throws InitializationError {@link InitializationError}
   * @throws IOException {@link IOException}
   */
  public JustTestLahRunner(Class<?> clazz) throws InitializationError, IOException {
    super(clazz);

    // Initialize Spring profiles and settings
    init();

    // Bridge logging to SLF4J
    bridgeLogging();

    if (properties.getProperty(CLOUD_PROVIDER, "local").equals("aws")) {
      LOG.info("Using io.github.martinschneider.justtestlah.awsdevicefarm.AWSTestRunner");
      awsRunner = getAWSRunner(clazz);
    } else {
      // load OpenCV library
      if (properties
          .getProperty(OPENCV_MODE_KEY, "client")
          .equals("client")) { // load the opencv library
        try {
          OpenCV.loadShared();
          OpenCV.loadLocally();
          System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        } catch (ExceptionInInitializerError exception) {
          LOG.error("Error loading OpenCV libraries", exception);
        }
      }

      String cucumberOptions = buildCucumberOptions();
      LOG.info("Setting cucumber options ({}) to {}", CUCUMBER_OPTIONS_KEY, cucumberOptions);
      System.setProperty(CUCUMBER_OPTIONS_KEY, cucumberOptions);

      ClassLoader classLoader = clazz.getClassLoader();
      Assertions.assertNoCucumberAnnotatedMethods(clazz);
      RuntimeOptions runtimeOptions = new RuntimeOptions(cucumberOptions);
      ResourceLoader resourceLoader = new MultiLoader(classLoader);
      FeatureLoader featureLoader = new FeatureLoader(resourceLoader);
      FeaturePathFeatureSupplier featureSupplier =
          new FeaturePathFeatureSupplier(featureLoader, runtimeOptions);
      // Parse the features early. Don't proceed when there are lexer errors
      final List<CucumberFeature> features = featureSupplier.get();
      List<String> featureList = new ArrayList<String>();
      features.forEach(
          feature -> {
            featureList.add(feature.getGherkinFeature().getFeature().getName());
          });
      LOG.debug(
          "Found {} feature(s) in {}: {}",
          features.size(),
          properties.getProperty(FEATURES_DIRECTORY_KEY),
          featureList);

      ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
      BackendSupplier backendSupplier =
          new BackendModuleBackendSupplier(resourceLoader, classFinder, runtimeOptions);
      this.eventBus = new TimeServiceEventBus(TimeService.SYSTEM);

      this.runnerSupplier =
          new ThreadLocalRunnerSupplier(runtimeOptions, eventBus, backendSupplier);
      this.filters = new Filters(runtimeOptions);
      this.junitOptions =
          new JUnitOptions(runtimeOptions.isStrict(), runtimeOptions.getJunitOptions());
      Plugins plugins = new Plugins(classLoader, new PluginFactory(), eventBus, runtimeOptions);
      final StepDefinitionReporter stepDefinitionReporter = plugins.stepDefinitionReporter();

      // Start the run before reading the features.
      // Allows the test source read events to be broadcast properly
      eventBus.send(new TestRunStarted(eventBus.getTime(), eventBus.getTimeMillis()));
      for (CucumberFeature feature : features) {
        feature.sendTestSourceRead(eventBus);
      }
      runnerSupplier.get().reportStepDefinitions(stepDefinitionReporter);
      addChildren(features);
    }
  }

  String buildCucumberOptions() {
    StringBuilder cucumberOptions = new StringBuilder();
    cucumberOptions.append("--tags '@" + properties.getProperty(PLATFORM_KEY, DEFAULT_PLATFORM));
    String tags = properties.getProperty(TAGS_KEY, null);
    if (tags != null) {
      // Prevent injection attacks
      if (tags.contains("'")) {
        throw new RuntimeException(
            String.format("Invalid character ' in tag expression: %s", tags));
      }
      // support legacy format (i.e. comma-separated list of tags without @)
      if (!tags.contains("@")) {
        for (String tag : tags.split(DELIMITER)) {
          cucumberOptions.append(" and @");
          cucumberOptions.append(tag);
        }
        cucumberOptions.append("'");
      } else // no format (tag expressions)
      {
        cucumberOptions.append(" and (");
        cucumberOptions.append(tags);
        cucumberOptions.append(")'");
      }
    }
    if (Boolean.parseBoolean(
        properties.getProperty(JUSTTESTLAH_SPRING_CONTEXT_KEY, Boolean.toString(true)))) {
      cucumberOptions.append(" --glue io.github.martinschneider.justtestlah.steps ");
    }
    cucumberOptions.append(" --glue ");
    cucumberOptions.append(properties.getProperty(STEPS_PACKAGE_KEY));
    cucumberOptions.append(" --plugin pretty --plugin html:report --plugin json:");
    cucumberOptions.append(
        properties.getProperty(CUCUMBER_REPORT_DIRECTORY_KEY, DEFAULT_CUCUMBER_REPORT_DIRECTORY));
    cucumberOptions.append("/cucumber.json ");
    cucumberOptions.append(properties.getProperty(FEATURES_DIRECTORY_KEY));
    cucumberOptions.append(" --strict");
    return cucumberOptions.toString();
  }

  private void bridgeLogging() {
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();
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
        eventBus.send(new TestRunFinished(eventBus.getTime(), eventBus.getTimeMillis()));
      }
    };
  }

  private void addChildren(List<CucumberFeature> cucumberFeatures) throws InitializationError {
    for (CucumberFeature cucumberFeature : cucumberFeatures) {
      FeatureRunner featureRunner =
          new FeatureRunner(cucumberFeature, filters, runnerSupplier, junitOptions);
      if (!featureRunner.isEmpty()) {
        children.add(featureRunner);
      }
    }
  }

  private void init() {
    // set the active Spring profile to the current platform
    String platform = properties.getProperty(PLATFORM_KEY);
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

  @Override
  public Description getDescription() {
    if (properties.getProperty(CLOUD_PROVIDER, "local").equals("aws")) {
      Description suiteDescription =
          Description.createSuiteDescription(AWS_JUNIT_SUITE_DESCRIPTION);
      suiteDescription.addChild(
          Description.createTestDescription("groupName", AWS_JUNIT_GROUP_DESCRIPTION));
      return suiteDescription;
    } else {
      return super.getDescription();
    }
  }

  /** this method uses reflection to avoid a compile-time dependency on justtestlah-awsdevicefarm */
  private Runner getAWSRunner(Class<?> clazz) {
    try {
      return (Runner)
          Class.forName("io.github.martinschneider.justtestlah.awsdevicefarm.AWSTestRunner")
              .getConstructor(Class.class)
              .newInstance(clazz);
    } catch (InstantiationException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException
        | NoSuchMethodException
        | SecurityException
        | ClassNotFoundException exception) {
      LOG.error(
          "Unable to create an instance of io.github.martinschneider.justtestlah.awsdevicefarm.AWSTestRunner. Ensure justtestlah-aws is on your classpath (check your Maven pom.xml).",
          exception);
    }
    return null;
  }

  @Override
  public void run(RunNotifier notifier) {
    if (properties.getProperty(CLOUD_PROVIDER, "local").equals("aws")) {
      awsRunner.run(notifier);
    } else {
      super.run(notifier);
    }
  }
}
