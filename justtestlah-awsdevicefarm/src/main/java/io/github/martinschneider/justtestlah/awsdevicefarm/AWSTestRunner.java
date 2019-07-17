package io.github.martinschneider.justtestlah.awsdevicefarm;

import com.amazonaws.services.devicefarm.model.DeviceFilter;
import com.amazonaws.services.devicefarm.model.DeviceSelectionConfiguration;
import com.amazonaws.services.devicefarm.model.GetRunRequest;
import com.amazonaws.services.devicefarm.model.Run;
import com.amazonaws.services.devicefarm.model.ScheduleRunRequest;
import com.amazonaws.services.devicefarm.model.ScheduleRunResult;
import com.amazonaws.services.devicefarm.model.ScheduleRunTest;
import com.amazonaws.services.devicefarm.model.TestType;
import com.amazonaws.services.devicefarm.model.UploadType;
import io.cucumber.junit.JustTestLahRunner;
import io.github.martinschneider.justtestlah.awsdevicefarm.utils.FormattingUtils;
import io.github.martinschneider.justtestlah.configuration.PropertiesHolder;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AWS Test runner.
 *
 * <p>This class goes through the complete process of executing tests on AWS.
 *
 * <ol>
 *   <li>Build the test package
 *   <li>Upload the test package to AWS (or use an existing upload)
 *   <li>Upload the app package to AWS (or use an existing upload)
 *   <li>Schedule the run
 *   <li>Collect the results
 * </ol>
 */
public class AWSTestRunner extends Runner {

  private static final Logger LOG = LoggerFactory.getLogger(AWSTestRunner.class);
  private static final int TIMEOUT = 60;

  private PropertiesHolder properties = new PropertiesHolder();
  private AWSService awsService = new AWSService();

  public AWSTestRunner(Class<?> testClass) {}

  public void run(RunNotifier notifier) {

    String projectArn = properties.getOptionalProperty("aws.projectArn");

    /** TEST SPEC FILE */
    LOG.info("Creating test spec file from template");
    String testSpecFile = null;
    try {
      testSpecFile = new TestSpecFactory(properties).createTestSpec();
    } catch (IOException exception) {
      LOG.error("Error creating test spec file", exception);
    }
    LOG.info("Uploading test spec file to AWS Devicefarm");
    String testSpecArn = null;
    testSpecArn = uploadTestSpec(projectArn, new File(testSpecFile));

    /** APP PACKAGE */
    String appArn = properties.getOptionalProperty("aws.appPackageArn");
    if (appArn == null || appArn.isEmpty()) {
      LOG.info("Uploading app package to AWS Devicefarm");
      appArn = uploadAppPackage(projectArn, new File(properties.getProperty("android.appPath")));
    } else {
      LOG.info("Using existing app package {}", appArn);
    }

    /** TEST PACKAGE */
    String testArn = properties.getOptionalProperty("aws.testPackageArn");
    if (testArn == null || testArn.isEmpty()) {
      LOG.info("Creating test package for AWS Devicefarm");
      File testPackage = null;
      try {
        testPackage = new TestPackager(properties).packageProjectForDeviceFarm();
      } catch (MavenInvocationException exception) {
        LOG.error("Error creating test package", exception);
      }
      LOG.info("Uploading test package to AWS Devicefarm");
      testArn = uploadTestPackage(projectArn, testPackage);
    } else {
      LOG.info("Using existing test package {}", testArn);
    }

    /** SCHEDULE RUN */
    ScheduleRunTest test = new ScheduleRunTest();
    test.setTestPackageArn(testArn);
    test.setType(TestType.APPIUM_JAVA_JUNIT);
    test.setTestSpecArn(testSpecArn);

    ScheduleRunRequest runRequest = new ScheduleRunRequest();
    TestConfigurationFactory testConfigurationFactory = new TestConfigurationFactory(properties);
    runRequest.setAppArn(appArn);
    runRequest.setConfiguration(testConfigurationFactory.getScheduleRunConfiguration());
    runRequest = setDevice(runRequest);
    String runName =
        properties.getProperty("aws.testpackage.name")
            + "_"
            + FormattingUtils.getCurrentTimestamp();
    runRequest.setExecutionConfiguration(testConfigurationFactory.getExecutionConfiguration());
    runRequest.setName(runName);
    runRequest.setProjectArn(projectArn);
    runRequest.setTest(test);

    LOG.info("Scheduling run on AWS Devicefarm");
    Description testDescription = getDescription().getChildren().get(0);
    notifier.fireTestStarted(testDescription);
    ScheduleRunResult runResult = awsService.getAws().scheduleRun(runRequest);
    String runArn = runResult.getRun().getArn();
    LOG.info("Scheduled run with arn {}", runArn);

    /** WAIT FOR TEST COMPLETION */
    if (waitForResult(runArn).equalsIgnoreCase("PASSED")) {
      notifier.fireTestFinished(testDescription);
    } else {
      notifier.fireTestFailure(
          new Failure(
              testDescription,
              new AssertionError(
                  "There were test failures. Please check the run on AWS Devicefarm.")));
    }
  }

  private String waitForResult(String runArn) {
    GetRunRequest getRunRequest = new GetRunRequest().withArn(runArn);
    Run run = awsService.getAws().getRun(getRunRequest).getRun();
    LOG.info("Waiting for run {}", run.getArn());
    long startTime = System.currentTimeMillis();
    long elapsedTime;
    String status;
    do {
      long currentStartTime = System.currentTimeMillis();
      elapsedTime = System.currentTimeMillis() - startTime;
      run = awsService.getAws().getRun(getRunRequest).getRun();
      status = run.getStatus();
      // TODO: use lambda expressions once SLF4j supports it
      LOG.info(
          "Test status: {} {} elapsed",
          String.format("%1$-15s", status),
          FormattingUtils.formatMilliseconds(elapsedTime));
      if (elapsedTime > TIMEOUT * 60 * 1000) {
        LOG.error("The tests didn't complete within the {} minute timeout!", TIMEOUT);
        break;
      }
      do {
        elapsedTime = System.currentTimeMillis() - currentStartTime;
      } while (elapsedTime < 10000);
    } while (!status.equals("COMPLETED"));
    LOG.info("{} device minutes used", run.getDeviceMinutes().getTotal());
    return run.getResult();
  }

  private String uploadAppPackage(String projectArn, File appPackage) {
    return awsService.upload(appPackage, projectArn, UploadType.ANDROID_APP, true).getArn();
  }

  private String uploadTestPackage(String projectArn, File testPackage) {
    return awsService
        .upload(testPackage, projectArn, UploadType.APPIUM_JAVA_JUNIT_TEST_PACKAGE, true)
        .getArn();
  }

  private String uploadTestSpec(String projectArn, File testSpecFile) {
    return awsService
        .upload(testSpecFile, projectArn, UploadType.APPIUM_JAVA_JUNIT_TEST_SPEC, true)
        .getArn();
  }

  private ScheduleRunRequest setDevice(ScheduleRunRequest runRequest) {
    List<DeviceFilter> deviceFilters =
        new DeviceFilterFactory(properties, awsService).getDeviceFilters();
    DeviceSelectionConfiguration deviceSelectionConfiguration = new DeviceSelectionConfiguration();
    deviceSelectionConfiguration.withFilters(deviceFilters).withMaxDevices(1);
    runRequest.setDeviceSelectionConfiguration(deviceSelectionConfiguration);
    return runRequest;
  }

  @Override
  /**
   * the description must match the one in {@link
   * io.github.martinschneider.justtestlah.junit.JustTestLahRunner}
   */
  public Description getDescription() {
    Description suiteDescription =
        Description.createSuiteDescription(JustTestLahRunner.AWS_JUNIT_SUITE_DESCRIPTION);
    Description groupDescription =
        Description.createTestDescription(
            "groupName", JustTestLahRunner.AWS_JUNIT_GROUP_DESCRIPTION);
    suiteDescription.addChild(groupDescription);
    suiteDescription.addChild(groupDescription);
    return suiteDescription;
  }
}
