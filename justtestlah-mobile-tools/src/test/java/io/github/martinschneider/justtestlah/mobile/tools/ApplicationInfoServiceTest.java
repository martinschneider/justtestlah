package io.github.martinschneider.justtestlah.mobile.tools;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Testdata is taken from https://github.com/OWASP/owasp-mstg Licensed under
 * https://creativecommons.org/licenses/by-sa/4.0/
 */
@RunWith(DataProviderRunner.class)
public class ApplicationInfoServiceTest {

  private ApplicationInfoService target = new ApplicationInfoService();

  @Test
  @UseDataProvider("testData")
  public void testApplicationMetaInfo(
      String fileName, String applicationName, String versionName, String versionCode) {
    ApplicationInfo appInfo =
        target.getAppInfo(
            ApplicationInfoServiceTest.class.getClassLoader().getResource(fileName).getFile());
    assertThat(appInfo.getApplicationName()).isEqualTo(applicationName);
    assertThat(appInfo.getVersionName()).isEqualTo(versionName);
    assertThat(appInfo.getVersionCode()).isEqualTo(versionCode);
  }

  @DataProvider
  public static Object[][] testData() {
    return new Object[][] {
      {"test.apk", "Uncrackable1", "1.0", "1"},
      {"test.ipa", "UnCrackable1", "1.0", "1"},
      {"test.app", "UnCrackable1", "1.0", "1"}
    };
  }
}
