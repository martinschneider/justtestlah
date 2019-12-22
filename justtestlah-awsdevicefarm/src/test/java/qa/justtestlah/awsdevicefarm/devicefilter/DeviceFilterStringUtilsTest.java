package qa.justtestlah.awsdevicefarm.devicefilter;

import static org.assertj.core.api.Assertions.assertThat;

import com.amazonaws.services.devicefarm.model.DeviceFilter;
import com.amazonaws.services.devicefarm.model.RuleOperator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class DeviceFilterStringUtilsTest {

  @Test
  public void testEqualsFilter() {
    List<DeviceFilter> deviceFilterList = new ArrayList<DeviceFilter>();
    deviceFilterList.add(
        new DeviceFilter()
            .withAttribute("attribute1")
            .withOperator(RuleOperator.EQUALS)
            .withValues("value1"));
    assertThat(DeviceFilterStringUtils.prettyPrintDeviceFilterList(deviceFilterList))
        .isEqualTo("[attribute1=value1]");
  }

  @Test
  public void testContainsFilter() {
    List<DeviceFilter> deviceFilterList = new ArrayList<DeviceFilter>();
    deviceFilterList.add(
        new DeviceFilter()
            .withAttribute("attribute1")
            .withOperator(RuleOperator.CONTAINS)
            .withValues("value1"));
    assertThat(DeviceFilterStringUtils.prettyPrintDeviceFilterList(deviceFilterList))
        .isEqualTo("[attribute1∋value1]");
  }

  @Test
  public void testInFilter() {
    List<DeviceFilter> deviceFilterList = new ArrayList<DeviceFilter>();
    deviceFilterList.add(
        new DeviceFilter()
            .withAttribute("attribute1")
            .withOperator(RuleOperator.IN)
            .withValues("value1"));
    assertThat(DeviceFilterStringUtils.prettyPrintDeviceFilterList(deviceFilterList))
        .isEqualTo("[attribute1∈value1]");
  }

  @Test
  public void testNotInFilter() {
    List<DeviceFilter> deviceFilterList = new ArrayList<DeviceFilter>();
    deviceFilterList.add(
        new DeviceFilter()
            .withAttribute("attribute1")
            .withOperator(RuleOperator.NOT_IN)
            .withValues("value1"));
    assertThat(DeviceFilterStringUtils.prettyPrintDeviceFilterList(deviceFilterList))
        .isEqualTo("[attribute1∉value1]");
  }

  @Test
  public void testLessThanFilter() {
    List<DeviceFilter> deviceFilterList = new ArrayList<DeviceFilter>();
    deviceFilterList.add(
        new DeviceFilter()
            .withAttribute("attribute1")
            .withOperator(RuleOperator.LESS_THAN)
            .withValues("value1"));
    assertThat(DeviceFilterStringUtils.prettyPrintDeviceFilterList(deviceFilterList))
        .isEqualTo("[attribute1<value1]");
  }

  @Test
  public void testLessThanOrEqualsFilter() {
    List<DeviceFilter> deviceFilterList = new ArrayList<DeviceFilter>();
    deviceFilterList.add(
        new DeviceFilter()
            .withAttribute("attribute1")
            .withOperator(RuleOperator.LESS_THAN_OR_EQUALS)
            .withValues("value1"));
    assertThat(DeviceFilterStringUtils.prettyPrintDeviceFilterList(deviceFilterList))
        .isEqualTo("[attribute1≤value1]");
  }

  @Test
  public void testGreaterThanFilter() {
    List<DeviceFilter> deviceFilterList = new ArrayList<DeviceFilter>();
    deviceFilterList.add(
        new DeviceFilter()
            .withAttribute("attribute1")
            .withOperator(RuleOperator.GREATER_THAN)
            .withValues("value1"));
    assertThat(DeviceFilterStringUtils.prettyPrintDeviceFilterList(deviceFilterList))
        .isEqualTo("[attribute1>value1]");
  }

  @Test
  public void testGreaterThanOrEqualsFilter() {
    List<DeviceFilter> deviceFilterList = new ArrayList<DeviceFilter>();
    deviceFilterList.add(
        new DeviceFilter()
            .withAttribute("attribute1")
            .withOperator(RuleOperator.GREATER_THAN_OR_EQUALS)
            .withValues("value1"));
    assertThat(DeviceFilterStringUtils.prettyPrintDeviceFilterList(deviceFilterList))
        .isEqualTo("[attribute1≥value1]");
  }

  @Test
  public void testMutlipleFilters() {
    List<DeviceFilter> deviceFilterList = new ArrayList<DeviceFilter>();
    deviceFilterList.add(
        new DeviceFilter()
            .withAttribute("attribute1")
            .withOperator(RuleOperator.EQUALS)
            .withValues("value1"));
    deviceFilterList.add(
        new DeviceFilter()
            .withAttribute("attribute2")
            .withOperator(RuleOperator.LESS_THAN)
            .withValues("value2"));
    assertThat(DeviceFilterStringUtils.prettyPrintDeviceFilterList(deviceFilterList))
        .isEqualTo("[attribute1=value1], [attribute2<value2]");
  }

  @Test
  public void testEmptyFilter() {
    assertThat(DeviceFilterStringUtils.prettyPrintDeviceFilterList(Collections.emptyList()))
        .isEqualTo("");
  }
}
