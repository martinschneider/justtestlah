package qa.justtestlah.awsdevicefarm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import qa.justtestlah.configuration.PropertiesHolder;

public class TestSpecFactoryTest {

  @Mock private PropertiesHolder properties;

  @Test
  public void testCreateTestSpec() throws IOException, URISyntaxException {
    MockitoAnnotations.initMocks(this);
    when(properties.getProperties()).thenReturn(new Properties());

    String[] expected =
        Files.readString(
                Paths.get(
                    this.getClass()
                        .getClassLoader()
                        .getResource("aws-devicefarm-testspec-expected.yaml")
                        .toURI()))
            .split(System.getProperty("line.separator"));
    String[] actual =
        Files.readString(Paths.get(new TestSpecFactory(properties).createTestSpec()))
            .split(System.getProperty("line.separator"));

    for (int i = 0; i < expected.length; i++) {
      assertThat(actual[i]).isEqualTo(expected[i]);
    }
  }
}
