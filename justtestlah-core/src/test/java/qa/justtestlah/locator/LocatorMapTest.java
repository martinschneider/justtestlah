package qa.justtestlah.locator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;
import org.junit.jupiter.api.Test;

public class LocatorMapTest {

  @Test
  public void testReplacePlaceholders() {
    Properties props = new Properties();
    props.put("key1", "value1");
    props.put("key2", "value2");
    props.put("key3", "value3");
    LocatorMap target = new LocatorMap(null, props);

    assertThat(target.replacePlaceholders(null)).isEqualTo(null);
    assertThat(target.replacePlaceholders("")).isEqualTo("");
    assertThat(target.replacePlaceholders("test")).isEqualTo("test");
    assertThat(target.replacePlaceholders("${key1}")).isEqualTo("value1");
    assertThat(target.replacePlaceholders("${key1} test")).isEqualTo("value1 test");
    assertThat(target.replacePlaceholders("test ${key1} test")).isEqualTo("test value1 test");
    assertThat(target.replacePlaceholders("${key1} test")).isEqualTo("value1 test");
    assertThat(target.replacePlaceholders("${key1}${key2}   ${key3}"))
        .isEqualTo("value1value2   value3");
    assertThat(target.replacePlaceholders("${")).isEqualTo("${");
  }

  @Test
  public void testFormatValue() {
    LocatorMap target = new LocatorMap();

    assertThat(target.formatValue(null)).isEqualTo(null);
    assertThat(target.formatValue("")).isEqualTo("");
    assertThat(target.formatValue(null, "dummy")).isEqualTo(null);
    assertThat(target.formatValue("", "dummy")).isEqualTo("");
    assertThat(target.formatValue("%s", "test")).isEqualTo("test");
    assertThat(target.formatValue("%s", "test", "dummy")).isEqualTo("test");
    assertThat(target.formatValue("%s%s   %s", "test1", "test2", "test3"))
        .isEqualTo("test1test2   test3");
  }
}
