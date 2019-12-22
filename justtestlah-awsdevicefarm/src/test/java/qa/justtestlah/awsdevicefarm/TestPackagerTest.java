package qa.justtestlah.awsdevicefarm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qa.justtestlah.configuration.PropertiesHolder;

public class TestPackagerTest {

  private Logger LOG = LoggerFactory.getLogger(TestPackagerTest.class);

  @Mock private PropertiesHolder properties;

  @Test
  public void testMavenPackaging()
      throws MavenInvocationException, MalformedURLException, IOException,
          ReflectiveOperationException {
    String currentPath = Paths.get("").toFile().getAbsolutePath();
    MockitoAnnotations.initMocks(this);
    when(properties.getProperty("aws.demo.path")).thenReturn(currentPath);
    when(properties.getProperty("aws.testpackage.name")).thenReturn("justtestlah-awsdevicefarm");
    LOG.info(
        "AWS Test package created: {}",
        new TestPackager(properties).packageProjectForDeviceFarm(false));
    assertThat(
            Files.exists(
                Paths.get(
                    currentPath
                        + File.separator
                        + "target"
                        + File.separator
                        + "justtestlah-awsdevicefarm.zip")))
        .isTrue();
  }
}
