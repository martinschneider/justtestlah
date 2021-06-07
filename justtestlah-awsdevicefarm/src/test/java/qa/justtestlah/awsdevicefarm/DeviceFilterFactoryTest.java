package qa.justtestlah.awsdevicefarm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.amazonaws.services.devicefarm.AWSDeviceFarm;
import com.amazonaws.services.devicefarm.model.Device;
import com.amazonaws.services.devicefarm.model.ListDevicesResult;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import qa.justtestlah.awsdevicefarm.devicefilter.DeviceFilterConstants;
import qa.justtestlah.awsdevicefarm.exception.AWSDeviceFarmException;
import qa.justtestlah.configuration.PropertiesHolder;

public class DeviceFilterFactoryTest {

  private DeviceFilterFactory target;

  @Mock private PropertiesHolder properties;

  @Mock private AWSService awsService;

  @Mock private AWSDeviceFarm awsDeviceFarm;

  @Mock private ListDevicesResult resultHighlyAvailable;

  @Mock private ListDevicesResult resultAvailable;

  @Mock private ListDevicesResult resultBusy;

  private AutoCloseable mocks;

  @BeforeEach
  public void setUp() {
    mocks = MockitoAnnotations.openMocks(this);
    when(awsService.getAws()).thenReturn(awsDeviceFarm);
    when(properties.getProperty("platform")).thenReturn("android");
    doReturn(resultHighlyAvailable)
        .when(awsDeviceFarm)
        .listDevices(
            argThat(
                argument ->
                    argument.getFilters().contains(DeviceFilterConstants.HIGHLY_AVAILABLE_FILTER)));
    doReturn(resultAvailable)
        .when(awsDeviceFarm)
        .listDevices(
            argThat(
                argument ->
                    argument.getFilters().contains(DeviceFilterConstants.AVAILABLE_FILTER)));
    doReturn(resultBusy)
        .when(awsDeviceFarm)
        .listDevices(
            argThat(argument -> argument.getFilters().contains(DeviceFilterConstants.BUSY_FILTER)));
    target = new DeviceFilterFactory(properties, awsService);
  }

  @AfterEach
  public void finish() throws Exception {
    mocks.close();
  }

  @Test
  public void testGetDeviceFilters() {
    when(resultHighlyAvailable.getDevices()).thenReturn(List.of(new Device().withName("test1")));
    assertThat(target.getDeviceFilters()).isNotNull().isNotEmpty();
  }

  @Test
  public void testGetDeviceFiltersNoHighlyAvailableDevices() {
    when(resultHighlyAvailable.getDevices()).thenReturn(Collections.emptyList());
    when(resultAvailable.getDevices()).thenReturn(List.of(new Device().withName("test1")));
    assertThat(target.getDeviceFilters()).isNotNull().isNotEmpty();
  }

  @Test
  public void testGetDeviceFiltersNoAvailableDevicesAndWaitIsTrue() {
    when(resultHighlyAvailable.getDevices()).thenReturn(Collections.emptyList());
    when(resultAvailable.getDevices()).thenReturn(Collections.emptyList());
    when(resultBusy.getDevices()).thenReturn(List.of(new Device().withName("test1")));
    when(properties.getProperty("aws.waitForDevice")).thenReturn("true");
    assertThat(target.getDeviceFilters()).isNotNull().isNotEmpty();
  }

  @Test
  public void testGetDeviceFiltersNoAvailableDevicesAndWaitIsFalse() {
    when(resultHighlyAvailable.getDevices()).thenReturn(Collections.emptyList());
    when(resultAvailable.getDevices()).thenReturn(Collections.emptyList());
    when(resultBusy.getDevices()).thenReturn(List.of(new Device().withName("test1")));
    when(properties.getProperty("aws.waitForDevice")).thenReturn("false");

    Throwable exception =
        assertThrows(
            AWSDeviceFarmException.class,
            () -> {
              target.getDeviceFilters();
            });

    assertThat(exception.getMessage())
        .as("check exception message")
        .isEqualTo("No matching devices available!");
  }
}
