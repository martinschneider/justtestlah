package qa.justtestlah.awsdevicefarm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import qa.justtestlah.configuration.PropertiesHolder;

public class TestSpecFactoryTest {

  @Mock private PropertiesHolder properties;

  @Test
  public void testCreateTestSpec() throws IOException, URISyntaxException {
    MockitoAnnotations.initMocks(this);
    when(properties.getProperties()).thenReturn(new Properties());

    List<String> expected =
        Files.readLines(
            new File(
                this.getClass()
                    .getClassLoader()
                    .getResource("aws-devicefarm-testspec-expected.yaml")
                    .toURI()),
            StandardCharsets.UTF_8);
    List<String> actual =
        Files.readLines(
            new File(new TestSpecFactory(properties).createTestSpec()), StandardCharsets.UTF_8);

    for (int i = 0; i < expected.size(); i++) {
      assertThat(expected.get(i)).isEqualTo(actual.get(i));
    }
  }
}
