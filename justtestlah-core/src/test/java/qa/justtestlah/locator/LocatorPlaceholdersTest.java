package qa.justtestlah.locator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;

public class LocatorPlaceholdersTest {
  private LocatorPlaceholders target;

  @Test
  public void testClasspath() {
    target = new LocatorPlaceholders("qa.justtestlah.locator", null);
    assertThat(target.getProps().containsKey("key1")).isEqualTo(true);
    assertThat(target.getProps().get("key1")).isEqualTo("value1");
    assertThat(target.getProps().containsKey("key2")).isEqualTo(false);
    assertThat(target.getProps().containsKey("key3")).isEqualTo(true);
    assertThat(target.getProps().get("key3")).isEqualTo("value3");
  }

  @Test
  public void testFilesystem() throws IOException {
    target =
        new LocatorPlaceholders("qa.justtestlah.locator.does.not.exist", createPropertiesFile());
    assertThat(target.getProps().containsKey("key1")).isEqualTo(true);
    assertThat(target.getProps().get("key1")).isEqualTo("value1*");
    assertThat(target.getProps().containsKey("key2")).isEqualTo(true);
    assertThat(target.getProps().get("key2")).isEqualTo("value2*");
    assertThat(target.getProps().containsKey("key3")).isEqualTo(false);
  }

  @Test
  public void testOverride() throws IOException {
    target = new LocatorPlaceholders("qa.justtestlah.locator", createPropertiesFile());
    assertThat(target.getProps().containsKey("key1")).isEqualTo(true);
    assertThat(target.getProps().get("key1")).isEqualTo("value1*");
    assertThat(target.getProps().containsKey("key2")).isEqualTo(true);
    assertThat(target.getProps().get("key2")).isEqualTo("value2*");
    assertThat(target.getProps().containsKey("key3")).isEqualTo(true);
    assertThat(target.getProps().get("key3")).isEqualTo("value3");
  }

  private static String createPropertiesFile() throws IOException {
    Path path = Files.createTempFile("placeholder", ".properties");
    File file = path.toFile();
    Files.write(path, "key1=value1*\nkey2=value2*".getBytes(StandardCharsets.UTF_8));
    file.deleteOnExit();
    return file.getAbsolutePath();
  }
}
