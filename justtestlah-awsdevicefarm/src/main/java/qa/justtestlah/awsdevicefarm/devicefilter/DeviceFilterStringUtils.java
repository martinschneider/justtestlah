package qa.justtestlah.awsdevicefarm.devicefilter;

import com.amazonaws.services.devicefarm.model.DeviceFilter;
import java.util.ArrayList;
import java.util.List;

/** Helper class to format {@link DeviceFilter} for log output */
public class DeviceFilterStringUtils {

  private DeviceFilterStringUtils() {}

  public static String prettyPrintDeviceFilterList(List<DeviceFilter> deviceFilters) {
    List<String> resultStrings = new ArrayList<>();
    for (DeviceFilter deviceFilter : deviceFilters) {
      resultStrings.add(prettyPrintDeviceFilter(deviceFilter));
    }
    return String.join(", ", resultStrings);
  }

  private static String prettyPrintDeviceFilter(DeviceFilter deviceFilter) {
    StringBuilder result = new StringBuilder("[");
    result.append(deviceFilter.getAttribute());
    result.append(formatOperator(deviceFilter.getOperator()));
    result.append(String.join(", ", deviceFilter.getValues()));
    result.append("]");
    return result.toString();
  }

  private static String formatOperator(String operator) {
    switch (operator) {
      case "EQUALS":
        return "=";
      case "CONTAINS":
        return "∋";
      case "IN":
        return "∈";
      case "NOT_IN":
        return "∉";
      case "LESS_THAN":
        return "<";
      case "LESS_THAN_OR_EQUALS":
        return "≤";
      case "GREATER_THAN":
        return ">";
      case "GREATER_THAN_OR_EQUALS":
        return "≥";
      default:
        return " " + operator + " ";
    }
  }
}
