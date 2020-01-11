package qa.justtestlah.awsdevicefarm;

import static org.assertj.core.api.Assertions.assertThat;

import com.amazonaws.services.devicefarm.model.BillingMethod;
import com.amazonaws.services.devicefarm.model.ExecutionConfiguration;
import com.amazonaws.services.devicefarm.model.ScheduleRunConfiguration;
import java.util.Properties;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import qa.justtestlah.configuration.PropertiesHolder;

public class TestConfigurationFactoryTest {

  private TestConfigurationFactory target;

  private PropertiesHolder properties = new PropertiesHolder();

  @Test
  public void testScheduleRunConfigurationTestTrue() {
    Properties props = new Properties();
    props.put("aws.runUnmetered", "true");
    props.put("aws.deviceLatitude", "100.0");
    props.put("aws.deviceLongitude", "50.0");
    props.put("aws.bluetooth", "true");
    props.put("aws.gps", "true");
    props.put("aws.nfc", "true");
    props.put("aws.wifi", "true");
    props.put("aws.extraDataArn", "extraPackage");
    ReflectionTestUtils.setField(properties, "props", props);
    target = new TestConfigurationFactory(properties);

    ScheduleRunConfiguration scheduleRunConfiguration = target.getScheduleRunConfiguration();
    BillingMethod billingMethod =
        Boolean.parseBoolean(props.getProperty("aws.runUnmetered"))
            ? BillingMethod.UNMETERED
            : BillingMethod.METERED;
    assertThat(scheduleRunConfiguration.getBillingMethod()).isEqualTo(billingMethod.toString());
    assertThat(scheduleRunConfiguration.getLocation().getLatitude())
        .isEqualTo(Double.parseDouble(props.getProperty("aws.deviceLatitude")));
    assertThat(scheduleRunConfiguration.getLocation().getLongitude())
        .isEqualTo(Double.parseDouble(props.getProperty("aws.deviceLongitude")));
    assertThat(scheduleRunConfiguration.getRadios().getBluetooth())
        .isEqualTo(Boolean.parseBoolean(props.getProperty("aws.bluetooth")));
    assertThat(scheduleRunConfiguration.getRadios().getGps())
        .isEqualTo(Boolean.parseBoolean(props.getProperty("aws.gps")));
    assertThat(scheduleRunConfiguration.getRadios().getNfc())
        .isEqualTo(Boolean.parseBoolean(props.getProperty("aws.nfc")));
    assertThat(scheduleRunConfiguration.getRadios().getWifi())
        .isEqualTo(Boolean.parseBoolean(props.getProperty("aws.wifi")));
    assertThat(scheduleRunConfiguration.getExtraDataPackageArn())
        .isEqualTo(props.getProperty("aws.extraDataArn"));
  }

  @Test
  public void testScheduleRunConfigurationTestFalse() {
    Properties props = new Properties();
    props.put("aws.runUnmetered", "false");
    props.put("aws.bluetooth", "false");
    props.put("aws.gps", "false");
    props.put("aws.nfc", "false");
    props.put("aws.wifi", "false");
    ReflectionTestUtils.setField(properties, "props", props);
    target = new TestConfigurationFactory(properties);

    ScheduleRunConfiguration scheduleRunConfiguration = target.getScheduleRunConfiguration();
    BillingMethod billingMethod =
        Boolean.parseBoolean(props.getProperty("aws.runUnmetered"))
            ? BillingMethod.UNMETERED
            : BillingMethod.METERED;
    assertThat(scheduleRunConfiguration.getBillingMethod()).isEqualTo(billingMethod.toString());
    assertThat(scheduleRunConfiguration.getRadios().getBluetooth())
        .isEqualTo(Boolean.parseBoolean(props.getProperty("aws.bluetooth")));
    assertThat(scheduleRunConfiguration.getRadios().getGps())
        .isEqualTo(Boolean.parseBoolean(props.getProperty("aws.gps")));
    assertThat(scheduleRunConfiguration.getRadios().getNfc())
        .isEqualTo(Boolean.parseBoolean(props.getProperty("aws.nfc")));
    assertThat(scheduleRunConfiguration.getRadios().getWifi())
        .isEqualTo(Boolean.parseBoolean(props.getProperty("aws.wifi")));
  }

  @Test
  public void testScheduleRunConfigurationTestRandom() {
    Properties props = new Properties();
    props.put("aws.runUnmetered", Boolean.toString(RandomUtils.nextBoolean()));
    props.put("aws.deviceLatitude", Double.toString(RandomUtils.nextDouble(0, 180)));
    props.put("aws.deviceLongitude", Double.toString(RandomUtils.nextDouble(0, 180)));
    props.put("aws.bluetooth", Boolean.toString(RandomUtils.nextBoolean()));
    props.put("aws.gps", Boolean.toString(RandomUtils.nextBoolean()));
    props.put("aws.nfc", Boolean.toString(RandomUtils.nextBoolean()));
    props.put("aws.wifi", Boolean.toString(RandomUtils.nextBoolean()));
    props.put("aws.extraDataArn", RandomStringUtils.randomAlphanumeric(20));
    ReflectionTestUtils.setField(properties, "props", props);
    target = new TestConfigurationFactory(properties);

    ScheduleRunConfiguration scheduleRunConfiguration = target.getScheduleRunConfiguration();
    BillingMethod billingMethod =
        Boolean.parseBoolean(props.getProperty("aws.runUnmetered"))
            ? BillingMethod.UNMETERED
            : BillingMethod.METERED;
    assertThat(scheduleRunConfiguration.getBillingMethod()).isEqualTo(billingMethod.toString());
    assertThat(scheduleRunConfiguration.getLocation().getLatitude())
        .isEqualTo(Double.parseDouble(props.getProperty("aws.deviceLatitude")));
    assertThat(scheduleRunConfiguration.getLocation().getLongitude())
        .isEqualTo(Double.parseDouble(props.getProperty("aws.deviceLongitude")));
    assertThat(scheduleRunConfiguration.getRadios().getBluetooth())
        .isEqualTo(Boolean.parseBoolean(props.getProperty("aws.bluetooth")));
    assertThat(scheduleRunConfiguration.getRadios().getGps())
        .isEqualTo(Boolean.parseBoolean(props.getProperty("aws.gps")));
    assertThat(scheduleRunConfiguration.getRadios().getNfc())
        .isEqualTo(Boolean.parseBoolean(props.getProperty("aws.nfc")));
    assertThat(scheduleRunConfiguration.getRadios().getWifi())
        .isEqualTo(Boolean.parseBoolean(props.getProperty("aws.wifi")));
    assertThat(scheduleRunConfiguration.getExtraDataPackageArn())
        .isEqualTo(props.getProperty("aws.extraDataArn"));
  }

  @Test
  public void testScheduleRunConfigurationTestDefaults() {
    Properties props = new Properties();
    ReflectionTestUtils.setField(properties, "props", props);
    target = new TestConfigurationFactory(properties);

    ScheduleRunConfiguration scheduleRunConfiguration = target.getScheduleRunConfiguration();
    assertThat(scheduleRunConfiguration.getBillingMethod())
        .isEqualTo(BillingMethod.UNMETERED.toString());
    assertThat(scheduleRunConfiguration.getLocation().getLatitude()).isEqualTo(1.3521);
    assertThat(scheduleRunConfiguration.getLocation().getLongitude()).isEqualTo(103.8198);
    assertThat(scheduleRunConfiguration.getRadios().getBluetooth()).isEqualTo(false);
    assertThat(scheduleRunConfiguration.getRadios().getGps()).isEqualTo(true);
    assertThat(scheduleRunConfiguration.getRadios().getNfc()).isEqualTo(true);
    assertThat(scheduleRunConfiguration.getRadios().getWifi()).isEqualTo(true);
    assertThat(scheduleRunConfiguration.getExtraDataPackageArn()).isEqualTo(null);
  }

  @Test
  public void testExecutionConfigurationTestTrue() {
    Properties props = new Properties();
    props.put("aws.accountsCleanUp", "true");
    props.put("aws.appPackagesCleanUp", "true");
    props.put("aws.jobTimeout", "10");
    props.put("aws.skipAppResign", "true");
    ReflectionTestUtils.setField(properties, "props", props);
    target = new TestConfigurationFactory(properties);

    ExecutionConfiguration executionConfiguration = target.getExecutionConfiguration();
    assertThat(executionConfiguration.getAccountsCleanup())
        .isEqualTo(Boolean.parseBoolean(props.getProperty("aws.accountsCleanUp")));
    assertThat(executionConfiguration.getAppPackagesCleanup())
        .isEqualTo(Boolean.parseBoolean(props.getProperty("aws.appPackagesCleanUp")));
    assertThat(executionConfiguration.getJobTimeoutMinutes())
        .isEqualTo(Integer.parseInt(props.getProperty("aws.jobTimeout")));
    assertThat(executionConfiguration.getSkipAppResign())
        .isEqualTo(Boolean.parseBoolean(props.getProperty("aws.skipAppResign")));
  }

  @Test
  public void testExecutionConfigurationTestRandom() {
    Properties props = new Properties();
    props.put("aws.accountsCleanUp", Boolean.toString(RandomUtils.nextBoolean()));
    props.put("aws.appPackagesCleanUp", Boolean.toString(RandomUtils.nextBoolean()));
    props.put("aws.jobTimeout", Integer.toString(RandomUtils.nextInt()));
    props.put("aws.skipAppResign", Boolean.toString(RandomUtils.nextBoolean()));
    ReflectionTestUtils.setField(properties, "props", props);
    target = new TestConfigurationFactory(properties);

    ExecutionConfiguration executionConfiguration = target.getExecutionConfiguration();
    assertThat(executionConfiguration.getAccountsCleanup())
        .isEqualTo(Boolean.parseBoolean(props.getProperty("aws.accountsCleanUp")));
    assertThat(executionConfiguration.getAppPackagesCleanup())
        .isEqualTo(Boolean.parseBoolean(props.getProperty("aws.appPackagesCleanUp")));
    assertThat(executionConfiguration.getJobTimeoutMinutes())
        .isEqualTo(Integer.parseInt(props.getProperty("aws.jobTimeout")));
    assertThat(executionConfiguration.getSkipAppResign())
        .isEqualTo(Boolean.parseBoolean(props.getProperty("aws.skipAppResign")));
  }

  @Test
  public void testExecutionConfigurationTestFalse() {
    Properties props = new Properties();
    props.put("aws.accountsCleanUp", "false");
    props.put("aws.appPackagesCleanUp", "false");
    props.put("aws.skipAppResign", "false");
    ReflectionTestUtils.setField(properties, "props", props);
    target = new TestConfigurationFactory(properties);

    ExecutionConfiguration executionConfiguration = target.getExecutionConfiguration();
    assertThat(executionConfiguration.getAccountsCleanup())
        .isEqualTo(Boolean.parseBoolean(props.getProperty("aws.accountsCleanUp")));
    assertThat(executionConfiguration.getAppPackagesCleanup())
        .isEqualTo(Boolean.parseBoolean(props.getProperty("aws.appPackagesCleanUp")));
    assertThat(executionConfiguration.getSkipAppResign())
        .isEqualTo(Boolean.parseBoolean(props.getProperty("aws.skipAppResign")));
  }

  @Test
  public void testExecutionConfigurationTestDefaults() {
    Properties props = new Properties();
    ReflectionTestUtils.setField(properties, "props", props);
    target = new TestConfigurationFactory(properties);

    ExecutionConfiguration executionConfiguration = target.getExecutionConfiguration();
    assertThat(executionConfiguration.getAccountsCleanup()).isEqualTo(true);
    assertThat(executionConfiguration.getAppPackagesCleanup()).isEqualTo(true);
    assertThat(executionConfiguration.getJobTimeoutMinutes()).isEqualTo(10);
    assertThat(executionConfiguration.getSkipAppResign()).isEqualTo(false);
  }
}
