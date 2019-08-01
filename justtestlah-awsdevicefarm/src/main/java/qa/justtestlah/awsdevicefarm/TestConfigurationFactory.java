package qa.justtestlah.awsdevicefarm;

import com.amazonaws.services.devicefarm.model.BillingMethod;
import com.amazonaws.services.devicefarm.model.ExecutionConfiguration;
import com.amazonaws.services.devicefarm.model.Location;
import com.amazonaws.services.devicefarm.model.Radios;
import com.amazonaws.services.devicefarm.model.ScheduleRunConfiguration;
import java.util.ArrayList;
import qa.justtestlah.configuration.PropertiesHolder;

/** Creates test configuration based on the configuration from {@link PropertiesHolder}. */
public class TestConfigurationFactory {

  private PropertiesHolder properties;

  public TestConfigurationFactory(PropertiesHolder properties) {
    this.properties = properties;
  }

  public ScheduleRunConfiguration getScheduleRunConfiguration() {
    ScheduleRunConfiguration configuration = new ScheduleRunConfiguration();
    if (Boolean.parseBoolean(properties.getProperty("aws.runUnmetered", "true"))) {
      configuration.setBillingMethod(BillingMethod.UNMETERED);
    } else {
      configuration.setBillingMethod(BillingMethod.METERED);
    }
    configuration.setAuxiliaryApps(new ArrayList<String>());
    configuration.setLocale("en_US");

    Location location = new Location();
    location.setLatitude(
        Double.parseDouble(properties.getProperty("aws.deviceLatitude", "1.3521")));
    location.setLongitude(
        Double.parseDouble(properties.getProperty("aws.deviceLongitude", "103.8198")));
    configuration.setLocation(location);

    Radios radios = new Radios();
    radios.setBluetooth(Boolean.parseBoolean(properties.getProperty("aws.bluetooth", "false")));
    radios.setGps(Boolean.parseBoolean(properties.getProperty("aws.gps", "true")));
    radios.setNfc(Boolean.parseBoolean(properties.getProperty("aws.nfc", "true")));
    radios.setWifi(Boolean.parseBoolean(properties.getProperty("aws.wifi", "true")));
    configuration.setRadios(radios);

    String extraDataArn = properties.getOptionalProperty("aws.extraDataArn");
    if (extraDataArn != null && !extraDataArn.isEmpty()) {
      configuration.setExtraDataPackageArn(extraDataArn);
    }
    return configuration;
  }

  public ExecutionConfiguration getExecutionConfiguration() {
    ExecutionConfiguration executionConfiguration = new ExecutionConfiguration();
    executionConfiguration.setAccountsCleanup(
        Boolean.parseBoolean(properties.getProperty("aws.accountsCleanup", "true")));
    executionConfiguration.setAppPackagesCleanup(
        Boolean.parseBoolean(properties.getProperty("aws.appPackagesCleanUp", "true")));
    executionConfiguration.setJobTimeoutMinutes(
        Integer.parseInt(properties.getProperty("aws.jobTimeout", "10")));
    executionConfiguration.setSkipAppResign(
        Boolean.parseBoolean(properties.getProperty("aws.skipAppResign", "false")));
    return executionConfiguration;
  }
}
