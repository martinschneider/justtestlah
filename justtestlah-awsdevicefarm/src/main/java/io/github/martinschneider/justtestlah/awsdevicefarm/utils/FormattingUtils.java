package io.github.martinschneider.justtestlah.awsdevicefarm.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/** Formatting helper methods. */
public class FormattingUtils {

  private static final String DURATION_FORMAT = "%02d:%02d:%02d";
  private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd_HH:mm";

  public static String formatMilliseconds(long millis) {
    return String.format(
        DURATION_FORMAT,
        TimeUnit.MILLISECONDS.toHours(millis),
        TimeUnit.MILLISECONDS.toMinutes(millis)
            - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
        TimeUnit.MILLISECONDS.toSeconds(millis)
            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
  }

  public static String getCurrentTimestamp() {
    return DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT).format(LocalDateTime.now());
  }
}
