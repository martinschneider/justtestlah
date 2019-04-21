package io.github.martinschneider.justtestlah.runners;

import java.lang.reflect.InvocationTargetException;
import org.junit.runner.notification.RunNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runner class for AWS Devicefarm. Uses reflection to avoid compile-time dependency on
 * `justtestlah-awsdevicefarm`.
 */
public class AWSTestRunner {

  private static Logger LOG = LoggerFactory.getLogger(AWSTestRunner.class);

  public void run(RunNotifier notifier) {
    Object awsDeviceFarmRunner = null;
    try {
      awsDeviceFarmRunner =
          Class.forName("io.github.martinschneider.justtestlah.awsdevicefarm.TestRunner")
              .getConstructor()
              .newInstance();
    } catch (InstantiationException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException
        | NoSuchMethodException
        | SecurityException
        | ClassNotFoundException exception) {
      LOG.error(
          "Unable to create an instance of io.github.martinschneider.justtestlah.awsdevicefarm.TestRunner. Ensure justtestlah-aws is on your classpath (check your Maven pom.xml).",
          exception);
    }
    try {
      awsDeviceFarmRunner.getClass().getMethod("run", RunNotifier.class).invoke(awsDeviceFarmRunner, notifier);
    } catch (IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException
        | NoSuchMethodException
        | SecurityException exception) {
      LOG.error(
          "Error invoking io.github.martinschneider.justtestlah.awsdevicefarm.TestRunner.run()",
          exception,
          exception);
    }
  }
}
