package qa.justtestlah.log;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.Context;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import qa.justtestlah.configuration.PropertiesHolder;
import qa.justtestlah.mobile.tools.ApplicationInfo;
import qa.justtestlah.mobile.tools.ApplicationInfoService;

public class ApplicationInfoEnricherTest {

  private ApplicationInfoEnricher target = new ApplicationInfoEnricher();

  @Mock private Context mockContext;
  @Mock private ApplicationInfoService applicationInfoService;
  @Mock private PropertiesHolder propertiesHolder;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
    Properties props = new Properties();
    props.put("platform", "android");
    when(propertiesHolder.getProperty("platform")).thenReturn("android");
    when(propertiesHolder.getProperty("android.appPath"))
        .thenReturn("/road/to/nowhere/android.apk");
    ReflectionTestUtils.setField(target, "props", propertiesHolder);
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
