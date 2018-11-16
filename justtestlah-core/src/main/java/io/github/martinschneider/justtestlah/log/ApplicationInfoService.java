package io.github.martinschneider.justtestlah.log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ApplicationInfoService {
  private final Logger LOG = LoggerFactory.getLogger(ApplicationInfoService.class);

  protected abstract ApplicationInfo getAppInfo(String platform, String appPath);

  /**
   * Executes a shell command and returns its output. This doesn't support multiline output.
   *
   * @param command the command to execute
   * @return the first line (!) of the output
   */
  public String executeCommand(String command) {
    LOG.info("Executing command {}", command);
    String[] shellCommand = {"/bin/sh", "-c", command};
    Process process;
    try {
      process = new ProcessBuilder(shellCommand).start();
      return Stream.of(process.getErrorStream(), process.getInputStream())
          .parallel()
          .map(
              (InputStream inputStream) -> {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                  return br.readLine(); // we only care about the first line
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              })
          .collect(Collectors.joining());
    } catch (IOException exception) {
      LOG.error(String.format("Error while executing command %s", command), exception);
    }
    return "";
  }
}
