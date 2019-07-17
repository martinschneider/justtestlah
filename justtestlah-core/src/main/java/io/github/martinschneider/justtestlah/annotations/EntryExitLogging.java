package io.github.martinschneider.justtestlah.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EntryExitLogging {
  /** OFF = no logging */
  int OFF = -1;

  /** TRACE level of logging. */
  int TRACE = 0;

  /** DEBUG level of logging. */
  int DEBUG = 1;

  /** INFO level of logging. */
  int INFO = 2;

  /** WARN level of logging. */
  int WARN = 3;

  /** ERROR level of logging. */
  int ERROR = 4;

  /**
   * The log level for the entry exit log message.
   *
   * @return log level
   */
  int entryExitLogLevel() default EntryExitLogging.DEBUG;

  /**
   * The log level for the entry exit log message.
   *
   * @return log level
   */
  int summaryLogLevel() default EntryExitLogging.INFO;
}
