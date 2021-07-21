package qa.justtestlah.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Helper class to write to the test log. The test log is meant to include all steps necessary to
 * reproduce a test scenario execution:
 *
 * <ul>
 *   <li>Test scenario and test steps
 *   <li>Webdriver actions (click, type, find etc.)
 *   <li>Optional entry-exit logs for steps and page object (and other) methods
 *   <li>Error messages
 * </ul>
 */
@Component
public class TestLogWriter {
  public static final String TESTLOG_LOGGER_NAME = "testlog";
  public static final int CUCUMBER_SCENARIO_INDENTATION = 0;
  public static final int CUCUMBER_STEP_INDENTATION = 2;
  public static final int ENTRY_EXIT_INDENTATION = 4;
  public static final int WEBDRIVER_INDENTATION = 6;
  public static final int ERROR_INDENTATION = 0;
  public static final char[] LOG_PREFIX = new char[] {'█', '█', '▓', '▓', '▒', '▒', '░', '░'};

  private static final Logger LOG = LoggerFactory.getLogger(TESTLOG_LOGGER_NAME);

  /**
   * @param logLevel log level {@link LogLevel}
   * @param indent optional indentation for this log line
   * @param message log message
   * @param params parameter (will be filled into
   *     <pre>
   *        {}
   *        </pre>
   *     in the log message)
   */
  public synchronized void log(int logLevel, int indent, String message, Object... params) {
    switch (logLevel) {
      case LogLevel.OFF:
        break;
      case LogLevel.TRACE:
        logAtTrace(indent, message, params);
        break;
      case LogLevel.DEBUG:
        logAtDebug(indent, message, params);
        break;
      case LogLevel.INFO:
        logAtInfo(indent, message, params);
        break;
      case LogLevel.WARN:
        logAtWarn(indent, message, params);
        break;
      case LogLevel.ERROR:
        logAtError(indent, message, params);
        break;
      default:
        logAtDebug(indent, message, params);
        break;
    }
  }

  private void logAtDebug(int indent, String message, Object... params) {
    LOG.debug(indentMessage(indent, message), params);
  }

  private void logAtInfo(int indent, String message, Object... params) {
    LOG.info(indentMessage(indent, message), params);
  }

  private void logAtWarn(int indent, String message, Object... params) {
    LOG.warn(indentMessage(indent, message), params);
  }

  private void logAtTrace(int indent, String message, Object... params) {
    LOG.trace(indentMessage(indent, message), params);
  }

  private void logAtError(int indent, String message, Object... params) {
    LOG.error(indentMessage(indent, message), params);
  }

  // indent the log message based on the indent-level
  private String indentMessage(int level, String message) {
    StringBuilder strBuilder = new StringBuilder(Thread.currentThread().getId() + " ");
    for (int i = 0; i < level; i++) {
      if (i < LOG_PREFIX.length) {
        strBuilder.append(LOG_PREFIX[i]);
      } else {
        strBuilder.append(' ');
      }
    }
    if (level > 0) {
      strBuilder.append(' ');
    }
    strBuilder.append(message);
    return strBuilder.toString();
  }
}
