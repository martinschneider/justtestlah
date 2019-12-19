package qa.justtestlah.awsdevicefarm.utils;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/** Formatting helper methods. */
public class FormattingUtils {

  private FormattingUtils() {}

  private static final String DURATION_FORMAT = "%02d:%02d:%02d";
  private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd_HH:mm";

  static Clock clock = Clock.systemDefaultZone();

  public static String formatMilliseconds(long millis) {
    return String.format(
        DURATION_FORMAT,
        TimeUnit.MILLISECONDS.toHours(millis),
        Math.abs(
            TimeUnit.MILLISECONDS.toMinutes(millis)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis))),
        Math.abs(
            TimeUnit.MILLISECONDS.toSeconds(millis)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));
  }

  public static String getCurrentTimestamp() {
    return DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT).format(LocalDateTime.now(clock));
  }
}
