package qa.justtestlah.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import qa.justtestlah.log.LogLevel;

/**
 * Annotation to mark methods for which entry/exit logging should be enabled.
 *
 * <p>See {@link qa.justtestlah.aop.EntryExitLoggingAspect}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EntryExitLogging {

  /**
   * The log level for the entry exit log message.
   *
   * @return log level
   */
  int entryExitLogLevel() default LogLevel.INFO;

  /**
   * The log level for the entry exit log message.
   *
   * @return log level
   */
  int summaryLogLevel() default LogLevel.OFF;
}
