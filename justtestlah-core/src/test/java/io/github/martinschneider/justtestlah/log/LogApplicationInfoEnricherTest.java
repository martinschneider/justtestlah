package io.github.martinschneider.justtestlah.log;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Context;
import io.github.martinschneider.justtestlah.configuration.PropertiesHolder;
import io.github.martinschneider.justtestlah.mobile.tools.ApplicationInfo;
import io.github.martinschneider.justtestlah.mobile.tools.ApplicationInfoService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class LogApplicationInfoEnricherTest {

  private LogApplicationInfoEnricher target = new LogApplicationInfoEnricher();

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
