package io.github.martinschneider.justtestlah.log;

import java.io.File;
import java.util.UUID;

public class MacOSXApplicationInfoService extends ApplicationInfoService {

  // ANDROID
  private static final String ANDROID_TOOLS_PATH = System.getProperty("android.tools.path");
  private static final String FETCH_APK_APPLICATION_NAME_COMMAND =
      "%s"
          + File.separator
          + "aapt dump badging %s | grep -o 'application-label:[^,]*' | cut -d':' -f2 | tr -d \"'\"";
  private static final String FETCH_APK_VERSION_NAME_COMMAND =
      "%s"
          + File.separator
          + "aapt dump badging %s | grep -o 'versionName=[^,]*'| cut -d'=' -f 2 | cut -d ' ' -f 1 | tr -d \"'\"";
  private static final String FETCH_APK_VERSION_CODE_COMMAND =
      "%s"
          + File.separator
          + "aapt dump badging %s | grep -o 'versionCode=[^,]*'| cut -d'=' -f 2 | cut -d ' ' -f 1 | tr -d \"'\"";

  // IOS
  // APP
  private static final String GET_IPHONE_VERSION_NAME_COMMAND =
      "defaults read %s" + File.separator + "Info CFBundleShortVersionString";
  private static final String GET_IPHONE_VERSION_CODE_COMMAND =
      "defaults read %s" + File.separator + "Info CFBundleVersion";
  private static final String GET_IPHONE_APPLICATION_NAME_COMMAND =
      "defaults read %s" + File.separator + "Info CFBundleDisplayName";

  // IPA
  private static final String TMP_FOLDER =
      File.separator + "tmp" + File.separator + UUID.randomUUID().toString();
  private static final String EXTRACT_IPA_COMMAND = "unzip %s -d %s";
  private static final String CLEANUP_IPA_COMMAND = "rm -rf " + TMP_FOLDER;
  private static final String GET_APP_PACKAGE_NAME_COMMAND =
      "ls " + TMP_FOLDER + File.separator + "Payload";

  @Override
  protected ApplicationInfo getAppInfo(String platform, String appPath) {
    if (platform.equalsIgnoreCase("android")) {
      return getAPKAppInfo(appPath);
    } else if (platform.equalsIgnoreCase("ios")) {
      if (appPath.endsWith(".ipa")) {
        return getIPAAppInfo(appPath);
      } else if (appPath.endsWith(".app")) {
        return getAPPAppInfo(appPath);
      }
    }
    return null;
  }

  private ApplicationInfo getAPPAppInfo(String appPath) {
    String versionName = executeCommand(String.format(GET_IPHONE_VERSION_NAME_COMMAND, appPath));
    String versionCode = executeCommand(String.format(GET_IPHONE_VERSION_CODE_COMMAND, appPath));
    String applicationName =
        executeCommand(String.format(GET_IPHONE_APPLICATION_NAME_COMMAND, appPath));
    return new ApplicationInfo(applicationName, versionName, versionCode);
  }

  private ApplicationInfo getIPAAppInfo(String appPath) {
    executeCommand(String.format(EXTRACT_IPA_COMMAND, appPath, TMP_FOLDER));
    String appPackage = executeCommand(GET_APP_PACKAGE_NAME_COMMAND);
    String versionName =
        executeCommand(
            String.format(GET_IPHONE_VERSION_NAME_COMMAND, getPathToExtractedApp(appPackage)));
    String versionCode =
        executeCommand(
            String.format(GET_IPHONE_VERSION_CODE_COMMAND, getPathToExtractedApp(appPackage)));
    String applicationName =
        executeCommand(
            String.format(GET_IPHONE_APPLICATION_NAME_COMMAND, getPathToExtractedApp(appPackage)));
    executeCommand(CLEANUP_IPA_COMMAND);
    return new ApplicationInfo(applicationName, versionName, versionCode);
  }

  private ApplicationInfo getAPKAppInfo(String appPath) {
    String versionName =
        executeCommand(String.format(FETCH_APK_VERSION_NAME_COMMAND, ANDROID_TOOLS_PATH, appPath));
    String versionCode =
        executeCommand(String.format(FETCH_APK_VERSION_CODE_COMMAND, ANDROID_TOOLS_PATH, appPath));
    String applicationName =
        executeCommand(
            String.format(FETCH_APK_APPLICATION_NAME_COMMAND, ANDROID_TOOLS_PATH, appPath));
    return new ApplicationInfo(applicationName, versionName, versionCode);
  }

  private String getPathToExtractedApp(String appPackage) {
    return TMP_FOLDER + File.separator + "Payload" + File.separator + appPackage;
  }
}
