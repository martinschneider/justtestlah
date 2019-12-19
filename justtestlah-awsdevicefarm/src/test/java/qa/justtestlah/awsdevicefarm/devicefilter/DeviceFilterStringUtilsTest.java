package qa.justtestlah.awsdevicefarm.devicefilter;

import com.amazonaws.services.devicefarm.model.DeviceFilter;
import com.amazonaws.services.devicefarm.model.RuleOperator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class DeviceFilterStringUtilsTest {

  @Test
  public void testSingleFilter() {
    List<DeviceFilter> deviceFilterList = new ArrayList<DeviceFilter>();
    deviceFilterList.add(new DeviceFilter().withAttribute("attribute1")
        .withOperator(RuleOperator.EQUALS).withValues("value1"));
    assertThat(DeviceFilterStringUtils.prettyPrintDeviceFilterList(deviceFilterList))
        .isEqualTo("[attribute1=value1]");
  }

  @Test
  public void testMutlipleFilters() {
    List<DeviceFilter> deviceFilterList = new ArrayList<DeviceFilter>();
    deviceFilterList.add(new DeviceFilter().withAttribute("attribute1")
        .withOperator(RuleOperator.EQUALS).withValues("value1"));
    deviceFilterList.add(new DeviceFilter().withAttribute("attribute2")
        .withOperator(RuleOperator.LESS_THAN).withValues("value2"));
    assertThat(DeviceFilterStringUtils.prettyPrintDeviceFilterList(deviceFilterList))
        .isEqualTo("[attribute1=value1], [attribute2<value2]");
  }

  @Test
  public void testEmptyFilter() {
    assertThat(DeviceFilterStringUtils.prettyPrintDeviceFilterList(Collections.emptyList()))
        .isEqualTo("");
  }
}
