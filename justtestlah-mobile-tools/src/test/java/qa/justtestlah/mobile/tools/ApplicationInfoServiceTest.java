package qa.justtestlah.mobile.tools;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import qa.justtestlah.mobile.tools.exception.MobileToolsException;

/**
 * Testdata is taken from https://github.com/OWASP/owasp-mstg Licensed under
 * https://creativecommons.org/licenses/by-sa/4.0/
 */
public class ApplicationInfoServiceTest {

  private ApplicationInfoService target = new ApplicationInfoService();

  @Test
  public void testNull() {
    assertThat(target.getAppInfo(null)).as("null check").isNull();
  }

  @Test
  public void testInvalidExtension() {
    assertThat(target.getAppInfo("app.abc"))
        .as("app file with wrong extension returns null")
        .isNull();
  }

  @Test
  public void testNonExistingApkFile() {
    assertThat(target.getAppInfo("doesnotexist.apk").toString())
        .as("non-existing app file shows as \"unknown\"")
        .isEqualTo("unknown unknown_unknown");
  }

  @Test
  public void testCorruptedIpaFile() {
    assertThat(target.getAppInfo("test_corrupt.ipa"))
        .as("corrupted ipa file should return null")
        .isNull();
  }

  @Test
  public void testCorruptedPlist() {
    Throwable exception =
        assertThrows(
            MobileToolsException.class,
            () -> {
              target.getDictionary(new File("doesnotexist"));
            });
    assertThat(exception.getMessage())
        .as("check exception message")
        .isEqualTo("Error reading dictionary from doesnotexist");
  }

  @ParameterizedTest
  @MethodSource("testData")
  public void testApplicationMetaInfo(
      String fileName, String applicationName, String versionName, String versionCode) {
    ApplicationInfo appInfo =
        target.getAppInfo(
            ApplicationInfoServiceTest.class.getClassLoader().getResource(fileName).getFile());
    assertThat(appInfo.getApplicationName())
        .as("check application name")
        .isEqualTo(applicationName);
    assertThat(appInfo.getVersionName()).as("check application version").isEqualTo(versionName);
    assertThat(appInfo.getVersionCode()).as("check version code").isEqualTo(versionCode);
  }

  private static Stream<Arguments> testData() {
    return Stream.of(
        Arguments.of("test.apk", "Uncrackable1", "1.0", "1"),
        Arguments.of("test.ipa", "UnCrackable1", "1.0", "1"),
        Arguments.of("test.app", "UnCrackable1", "1.0", "1"));
  }
}
