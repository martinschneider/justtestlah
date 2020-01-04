package qa.justtestlah.galen;

import com.galenframework.reports.GalenTestInfo;
import java.util.LinkedList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GalenTestInfoContainer {
  private List<GalenTestInfo> galenTests = new LinkedList<>();

  public List<GalenTestInfo> get() {
    return galenTests;
  }

  public void add(GalenTestInfo testInfo) {
    galenTests.add(testInfo);
  }
}
