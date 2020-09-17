package io.cucumber.junit;

import static io.cucumber.junit.FileNameCompatibleNames.uniqueSuffix;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import io.cucumber.core.eventbus.EventBus;
import io.cucumber.core.feature.FeatureParser;
import io.cucumber.core.filter.Filters;
import io.cucumber.core.gherkin.Feature;
import io.cucumber.core.gherkin.Pickle;
import io.cucumber.core.options.CucumberOptionsAnnotationParser;
import io.cucumber.core.options.CucumberProperties;
import io.cucumber.core.options.CucumberPropertiesParser;
import io.cucumber.core.options.RuntimeOptions;
import io.cucumber.core.plugin.PluginFactory;
import io.cucumber.core.plugin.Plugins;
import io.cucumber.core.resource.ClassLoaders;
import io.cucumber.core.runtime.BackendServiceLoader;
import io.cucumber.core.runtime.BackendSupplier;
import io.cucumber.core.runtime.CucumberExecutionContext;
import io.cucumber.core.runtime.ExitStatus;
import io.cucumber.core.runtime.FeaturePathFeatureSupplier;
import io.cucumber.core.runtime.ObjectFactoryServiceLoader;
import io.cucumber.core.runtime.ObjectFactorySupplier;
import io.cucumber.core.runtime.ScanningTypeRegistryConfigurerSupplier;
import io.cucumber.core.runtime.ThreadLocalObjectFactorySupplier;
import io.cucumber.core.runtime.ThreadLocalRunnerSupplier;
import io.cucumber.core.runtime.TimeServiceEventBus;
import io.cucumber.core.runtime.TypeRegistryConfigurerSupplier;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import qa.justtestlah.configuration.CucumberOptionsBuilder;
import qa.justtestlah.configuration.Platform;
import qa.justtestlah.configuration.PropertiesHolder;

/**
 * Custom JUnit runner to dynamically set Cucumber options. Based on {@link
 * io.cucumber.junit.Cucumber}.
 */
public class JustTestLahRunner extends ParentRunner<ParentRunner<?>> {

  private static final String CLOUDPROVIDER_AWS = "aws";
  private static final String CLOUDPROVIDER_LOCAL = "local";
  public static final String AWS_JUNIT_GROUP_DESCRIPTION = "Test results";
  public static final String AWS_JUNIT_SUITE_DESCRIPTION = "AWS Devicefarm execution";

  private static final Logger LOG = LoggerFactory.getLogger(JustTestLahRunner.class);

  private List<ParentRunner<?>> children = new ArrayList<>();
  private List<Feature> features = new ArrayList<>();
  private Plugins plugins = null;
  private EventBus bus = null;
  private PropertiesHolder properties = new PropertiesHolder();
  private boolean multiThreadingAssumed = false;
  private CucumberExecutionContext context = null;

  private static final String CLOUD_PROVIDER = "cloudprovider";
  private static final String PLATFORM_KEY = "platform";
  private static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";

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

    if (properties.getProperty(CLOUD_PROVIDER, CLOUDPROVIDER_LOCAL).equals(CLOUDPROVIDER_AWS)) {
      LOG.info("Using qa.justtestlah.awsdevicefarm.AWSTestRunner");
      awsRunner = getAWSRunner(clazz);
    } else {
      CucumberOptionsBuilder.setCucumberOptions(properties);
      initCucumber(clazz);
    }
  }

  /**
   * This is the code taken from {@link io.cucumber.junit.Cucumber}
   *
   * @param clazz {@link Class}
   * @throws InitializationError {@link InitializationError}
   */
  private void initCucumber(Class<?> clazz) throws InitializationError {
    Assertions.assertNoCucumberAnnotatedMethods(clazz);

    // Parse the options early to provide fast feedback about invalid
    // options
    RuntimeOptions propertiesFileOptions =
        new CucumberPropertiesParser().parse(CucumberProperties.fromPropertiesFile()).build();

    RuntimeOptions annotationOptions =
        new CucumberOptionsAnnotationParser()
            .withOptionsProvider(new JUnitCucumberOptionsProvider())
            .parse(clazz)
            .build(propertiesFileOptions);

    RuntimeOptions environmentOptions =
        new CucumberPropertiesParser()
            .parse(CucumberProperties.fromEnvironment())
            .build(annotationOptions);

    RuntimeOptions runtimeOptions =
        new CucumberPropertiesParser()
            .parse(CucumberProperties.fromSystemProperties())
            .enablePublishPlugin()
            .build(environmentOptions);

    // Next parse the junit options
    JUnitOptions junitPropertiesFileOptions =
        new JUnitOptionsParser().parse(CucumberProperties.fromPropertiesFile()).build();

    JUnitOptions junitAnnotationOptions =
        new JUnitOptionsParser().parse(clazz).build(junitPropertiesFileOptions);

    JUnitOptions junitEnvironmentOptions =
        new JUnitOptionsParser()
            .parse(CucumberProperties.fromEnvironment())
            .build(junitAnnotationOptions);

    JUnitOptions junitOptions =
        new JUnitOptionsParser()
            .parse(CucumberProperties.fromSystemProperties())
            .build(junitEnvironmentOptions);

    this.bus = new TimeServiceEventBus(Clock.systemUTC(), UUID::randomUUID);

    // Parse the features early. Don't proceed when there are lexer errors
    FeatureParser parser = new FeatureParser(bus::generateId);
    Supplier<ClassLoader> classLoader = ClassLoaders::getDefaultClassLoader;
    FeaturePathFeatureSupplier featureSupplier =
        new FeaturePathFeatureSupplier(classLoader, runtimeOptions, parser);
    this.features = featureSupplier.get();

    // Create plugins after feature parsing to avoid the creation of empty
    // files on lexer errors.
    this.plugins = new Plugins(new PluginFactory(), runtimeOptions);
    ExitStatus exitStatus = new ExitStatus(runtimeOptions);
    this.plugins.addPlugin(exitStatus);

    ObjectFactoryServiceLoader objectFactoryServiceLoader =
        new ObjectFactoryServiceLoader(runtimeOptions);
    ObjectFactorySupplier objectFactorySupplier =
        new ThreadLocalObjectFactorySupplier(objectFactoryServiceLoader);
    BackendSupplier backendSupplier =
        new BackendServiceLoader(clazz::getClassLoader, objectFactorySupplier);
    TypeRegistryConfigurerSupplier typeRegistryConfigurerSupplier =
        new ScanningTypeRegistryConfigurerSupplier(classLoader, runtimeOptions);
    ThreadLocalRunnerSupplier runnerSupplier =
        new ThreadLocalRunnerSupplier(
            runtimeOptions,
            bus,
            backendSupplier,
            objectFactorySupplier,
            typeRegistryConfigurerSupplier);
    this.context = new CucumberExecutionContext(bus, exitStatus, runnerSupplier);
    Predicate<Pickle> filters = new Filters(runtimeOptions);

    Map<Optional<String>, List<Feature>> groupedByName =
        features.stream().collect(groupingBy(Feature::getName));
    this.children =
        features.stream()
            .map(
                feature -> {
                  Integer uniqueSuffix = uniqueSuffix(groupedByName, feature, Feature::getName);
                  return FeatureRunner.create(
                      feature, uniqueSuffix, filters, runnerSupplier, junitOptions);
                })
            .filter(runner -> !runner.isEmpty())
            .collect(toList());

    LOG.info(
        "Found {} feature(s) in {}: {}",
        features.size(),
        System.getProperty("cucumber.features"),
        features);
  }

  @Override
  protected List<ParentRunner<?>> getChildren() {
    return children;
  }

  @Override
  protected Description describeChild(ParentRunner<?> child) {
    return child.getDescription();
  }

  @Override
  protected void runChild(ParentRunner<?> child, RunNotifier notifier) {
    child.run(notifier);
  }

  @Override
  protected Statement childrenInvoker(RunNotifier notifier) {
    Statement runFeatures = super.childrenInvoker(notifier);
    return new RunCucumber(runFeatures);
  }

  @Override
  public void setScheduler(RunnerScheduler scheduler) {
    super.setScheduler(scheduler);
    multiThreadingAssumed = true;
  }

  class RunCucumber extends Statement {

    private final Statement runFeatures;

    RunCucumber(Statement runFeatures) {
      this.runFeatures = runFeatures;
    }

    @Override
    public void evaluate() throws Throwable {
      if (multiThreadingAssumed) {
        plugins.setSerialEventBusOnEventListenerPlugins(bus);
      } else {
        plugins.setEventBusOnEventListenerPlugins(bus);
      }

      context.startTestRun();
      features.forEach(context::beforeFeature);

      try {
        runFeatures.evaluate();
      } finally {
        context.finishTestRun();
      }
    }
  }

  private void bridgeLogging() {
    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();
  }

  private void init() {
    // set the active Spring profile to the current platform
    String platform = properties.getProperty(PLATFORM_KEY);
    if (platform == null || platform.isEmpty()) {
      LOG.info("No platform specified. Using default ({})", Platform.DEFAULT);
      platform = Platform.DEFAULT.getPlatformName();
      System.setProperty(PLATFORM_KEY, platform);
    }
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
    if (properties.getProperty(CLOUD_PROVIDER, CLOUDPROVIDER_LOCAL).equals(CLOUDPROVIDER_AWS)) {
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
          Class.forName("qa.justtestlah.awsdevicefarm.AWSTestRunner")
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
          "Unable to create an instance of qa.justtestlah.awsdevicefarm.AWSTestRunner. Ensure justtestlah-aws is on your classpath (check your Maven pom.xml).",
          exception);
    }
    return null;
  }

  @Override
  public void run(RunNotifier notifier) {
    if (properties.getProperty(CLOUD_PROVIDER, CLOUDPROVIDER_LOCAL).equals(CLOUDPROVIDER_AWS)) {
      awsRunner.run(notifier);
    } else {
      super.run(notifier);
    }
  }
}
