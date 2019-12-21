package qa.justtestlah.awsdevicefarm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.amazonaws.services.devicefarm.model.ExecutionConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import qa.justtestlah.configuration.PropertiesHolder;

public class TestConfigurationFactoryTest {

  private TestConfigurationFactory target;

  @Mock private PropertiesHolder properties;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    when(properties.getProperty(eq("aws.accountsCleanup"), anyString())).thenReturn("true");
    when(properties.getProperty(eq("aws.appPackagesCleanUp"), anyString())).thenReturn("true");
    when(properties.getProperty(eq("aws.jobTimeout"), anyString())).thenReturn("10");
    when(properties.getProperty(eq("aws.skipAppResign"), anyString())).thenReturn("true");
    target = new TestConfigurationFactory(properties);
  }

  @Test
  public void testExecutionConfigurationTest() {
    ExecutionConfiguration executionConfiguration = target.getExecutionConfiguration();
    assertThat(executionConfiguration.getAccountsCleanup())
        .isEqualTo(Boolean.parseBoolean(properties.getProperty("aws.accountsCleanup", "true")));
    assertThat(executionConfiguration.getAppPackagesCleanup())
        .isEqualTo(Boolean.parseBoolean(properties.getProperty("aws.appPackagesCleanUp", "true")));
    assertThat(executionConfiguration.getJobTimeoutMinutes())
        .isEqualTo(Integer.parseInt(properties.getProperty("aws.jobTimeout", "10")));
    assertThat(executionConfiguration.getSkipAppResign())
        .isEqualTo(Boolean.parseBoolean(properties.getProperty("aws.skipAppResign", "true")));
  }
}
