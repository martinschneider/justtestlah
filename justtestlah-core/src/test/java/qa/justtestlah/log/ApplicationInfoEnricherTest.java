package qa.justtestlah.log;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Context;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import qa.justtestlah.configuration.PropertiesHolder;
import qa.justtestlah.mobile.tools.ApplicationInfo;
import qa.justtestlah.mobile.tools.ApplicationInfoService;

public class ApplicationInfoEnricherTest {

  private ApplicationInfoEnricher target = new ApplicationInfoEnricher();

  @Mock private Context mockContext;
  @Mock private ApplicationInfoService applicationInfoService;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    System.setProperty(PropertiesHolder.JUST_TEST_LAH_LOCATION_KEY, "");
  }

  @Test
  public void testStart() {
    target.setContext(new LoggerContext());
    when(applicationInfoService.getAppInfo("/road/to/nowhere/android.apk"))
        .thenReturn(new ApplicationInfo("dummy", "test", "foo"));
    target.setApplicationInfoService(applicationInfoService);
    target.start();
    assertThat(target.getContext().getProperty("appInfo")).isEqualTo("ANDROID dummy test_foo");
  }

  @Test
  public void testStartOnlyOnce() {
    when(mockContext.getProperty("appInfo")).thenReturn("versionInfo");
    target.setContext(mockContext);
    target.start();
    assertThat(target.getContext().getProperty("appInfo")).isEqualTo("versionInfo");
    verify(mockContext, times(1)).getProperty("appInfo");
    target.start();
    verify(mockContext, times(1)).getProperty("appInfo");
  }
}
