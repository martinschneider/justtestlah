package qa.justtestlah.awsdevicefarm;

import com.amazonaws.services.devicefarm.model.Device;
import com.amazonaws.services.devicefarm.model.DeviceFilter;
import com.amazonaws.services.devicefarm.model.ListDevicesRequest;
import com.amazonaws.services.devicefarm.model.RuleOperator;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qa.justtestlah.awsdevicefarm.devicefilter.DeviceFilterConstants;
import qa.justtestlah.awsdevicefarm.devicefilter.DeviceFilterStringUtils;
import qa.justtestlah.awsdevicefarm.exception.AWSDeviceFarmException;
import qa.justtestlah.configuration.PropertiesHolder;

/** Creates a device filter based on the configuration from {@link PropertiesHolder}. */
public class DeviceFilterFactory {

  private static final String OS_VERSION = "OS_VERSION";

  private static final Logger LOG = LoggerFactory.getLogger(DeviceFilterFactory.class);

  private PropertiesHolder properties;
  private AWSService awsService;

  /**
   * @param properties {@link PropertiesHolder}
   * @param awsService {@link AWSService}
   */
  public DeviceFilterFactory(PropertiesHolder properties, AWSService awsService) {
    this.properties = properties;
    this.awsService = awsService;
  }

  public List<DeviceFilter> getDeviceFilters() {
    List<DeviceFilter> deviceFilters = new ArrayList<>();
    String platform = properties.getProperty("platform");
    // if device filters are used at least the platform must be specified
    if (platform == null || platform.isEmpty()) {
      throw new UnsupportedOperationException("platform property must be set");
    }
    deviceFilters.add(DeviceFilterConstants.HIGHLY_AVAILABLE_FILTER);
    addFilter(deviceFilters, "PLATFORM", platform);
    addFilter(
        deviceFilters,
        OS_VERSION,
        RuleOperator.GREATER_THAN_OR_EQUALS,
        properties.getOptionalProperty("aws.minOsVersion"));
    addFilter(
        deviceFilters,
        OS_VERSION,
        RuleOperator.LESS_THAN_OR_EQUALS,
        properties.getOptionalProperty("aws.maxOsVersion"));
    addFilter(deviceFilters, OS_VERSION, properties.getOptionalProperty("aws.osVersion"));
    addFilter(
        deviceFilters, "MODEL", RuleOperator.CONTAINS, properties.getOptionalProperty("aws.model"));
    addFilter(deviceFilters, "FORM_FACTOR", properties.getOptionalProperty("aws.formFactor"));
    addFilter(deviceFilters, "MANUFACTURER", properties.getOptionalProperty("aws.manufacturer"));
    return deviceFilters;
  }

  private List<DeviceFilter> addFilter(
      List<DeviceFilter> deviceFilters, String attribute, String... values) {
    return addFilter(deviceFilters, attribute, RuleOperator.EQUALS, values);
  }

  private List<DeviceFilter> addFilter(
      List<DeviceFilter> deviceFilters, String attribute, RuleOperator operator, String... values) {
    if (values.length > 0 && values[0] != null && !values[0].isEmpty()) {
      deviceFilters.add(
          new DeviceFilter().withAttribute(attribute).withOperator(operator).withValues(values));
      adaptAvailabilityFilter(deviceFilters);
    }
    return deviceFilters;
  }

  private void adaptAvailabilityFilter(List<DeviceFilter> deviceFilters) {
    List<Device> availableDevices = getAvailableDevices(deviceFilters);
    if (availableDevices.isEmpty()) {
      if (deviceFilters.contains(DeviceFilterConstants.HIGHLY_AVAILABLE_FILTER)) {
        LOG.warn(
            "No matching devices are HIGHLY_AVAILABLE, changing availability filter to AVAILABLE");
        deviceFilters.remove(DeviceFilterConstants.HIGHLY_AVAILABLE_FILTER);
        deviceFilters.add(DeviceFilterConstants.AVAILABLE_FILTER);
        adaptAvailabilityFilter(deviceFilters);
      } else if (deviceFilters.contains(DeviceFilterConstants.AVAILABLE_FILTER)
          && Boolean.parseBoolean(properties.getProperty("aws.waitForDevice"))) {
        LOG.warn(
            "No matching devices are AVAILABLE, changing availability filter to BUSY (test execution will be queued)");
        deviceFilters.remove(DeviceFilterConstants.AVAILABLE_FILTER);
        deviceFilters.add(DeviceFilterConstants.BUSY_FILTER);
        adaptAvailabilityFilter(deviceFilters);
      } else {
        LOG.error("No matching devices available!");
        throw new AWSDeviceFarmException("No matching devices available!");
      }
    }
  }

  private List<Device> getAvailableDevices(final List<DeviceFilter> deviceFilters) {
    List<Device> devices =
        awsService
            .getAws()
            .listDevices(new ListDevicesRequest().withFilters(deviceFilters))
            .getDevices();
    LOG.atInfo()
        .addArgument(() -> devices.size())
        .addArgument(DeviceFilterStringUtils.prettyPrintDeviceFilterList(deviceFilters))
        .log("{} device(s) matching {}");
    return devices;
  }
}
