package qa.justtestlah.awsdevicefarm.utils;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class FormattingUtilsTest {

  @Test
  public void testTimestamp() {
    LocalDateTime timestamp = LocalDateTime.of(1984, 8, 20, 19, 1, 2, 300);
    FormattingUtils.clock =
        Clock.fixed(timestamp.toInstant(ZoneOffset.ofHours(2)), ZoneId.of("Europe/Bratislava"));
    assertThat(FormattingUtils.getCurrentTimestamp()).isEqualTo("1984-08-20_19:01");
  }

  @Test
  public void testFormatting() {
    assertThat(FormattingUtils.formatMilliseconds(123456789L)).isEqualTo("34:17:36");
    assertThat(FormattingUtils.formatMilliseconds(0L)).isEqualTo("00:00:00");
  }

  @Test
  public void testFormattingZero() {
    assertThat(FormattingUtils.formatMilliseconds(0L)).isEqualTo("00:00:00");
  }

  @Test
  public void testFormattingNegative() {
    assertThat(FormattingUtils.formatMilliseconds(-123456789L)).isEqualTo("-34:17:36");
  }
}
