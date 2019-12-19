package qa.justtestlah.awsdevicefarm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.amazonaws.services.devicefarm.AWSDeviceFarm;
import com.amazonaws.services.devicefarm.model.Device;
import com.amazonaws.services.devicefarm.model.ListDevicesResult;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import qa.justtestlah.configuration.PropertiesHolder;

public class DeviceFilterFactoryTest {

  private DeviceFilterFactory target;

  @Mock private PropertiesHolder properties;

  @Mock private AWSService awsService;

  @Mock private AWSDeviceFarm awsDeviceFarm;

  @Mock private ListDevicesResult listDevicesResult;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    when(awsService.getAws()).thenReturn(awsDeviceFarm);
    when(properties.getProperty("platform")).thenReturn("android");
    when(awsDeviceFarm.listDevices(any())).thenReturn(listDevicesResult);

    List<Device> deviceList = new ArrayList<Device>();
    deviceList.add(new Device().withName("test1"));
    deviceList.add(new Device().withName("test2"));

    when(listDevicesResult.getDevices()).thenReturn(deviceList);
    target = new DeviceFilterFactory(properties, awsService);
  }

  @Test
  public void testGetDeviceFilters() {
    assertThat(target.getDeviceFilters().size()).isEqualTo(2);
  }
}
