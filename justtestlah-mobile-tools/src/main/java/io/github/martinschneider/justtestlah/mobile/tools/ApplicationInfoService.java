package io.github.martinschneider.justtestlah.mobile.tools;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListFormatException;
import com.dd.plist.PropertyListParser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Comparator;
import java.util.UUID;
import javax.xml.parsers.ParserConfigurationException;
import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/** Read metadata from APK, IPA and APP files. */
public class ApplicationInfoService {

  private final Logger LOG = LoggerFactory.getLogger(ApplicationInfoService.class);

  public ApplicationInfo getAppInfo(String appPath) {
    if (appPath.endsWith(".apk")) {
      return getAPKAppInfo(appPath);
    } else if (appPath.endsWith(".ipa")) {
      return getIPAAppInfo(appPath);
    } else if (appPath.endsWith(".app")) {
      return getAPPAppInfo(appPath);
    } else {
      LOG.warn(
          String.format(
              "App package %s does not have any of the expected extensions: .apk, .ipa or .app",
              appPath));
      return null;
    }
  }

  private ApplicationInfo getAPPAppInfo(String appPath) {
    return getAppInfoFromDictionary(
        getDictionary(new File(appPath + File.separator + "Info.plist")));
  }

  private ApplicationInfo getIPAAppInfo(String appPath) {
    // extract IPA contents
    String tmpFolder = File.separator + "tmp" + File.separator + UUID.randomUUID().toString();
    try {
      ZipFile zipFile = new ZipFile(appPath);
      zipFile.extractAll(tmpFolder);
    } catch (ZipException exception) {
      LOG.error(String.format("Error extracting files from %s", appPath), exception);
    }

    // retrieve application info
    ApplicationInfo appInfo =
        getAppInfoFromDictionary(
            getDictionary(
                new File(
                    new File(tmpFolder + File.separator + "Payload")
                            .listFiles()[0].getAbsolutePath()
                        + File.separator
                        + "Info.plist")));

    // delete temporary files
    try {
      Files.walk(Paths.get(tmpFolder))
          .sorted(Comparator.reverseOrder())
          .map(Path::toFile)
          .forEach(File::delete);
    } catch (IOException exception) {
      LOG.error(String.format("Error deleting %s", tmpFolder), exception);
    }
    return appInfo;
  }

  private ApplicationInfo getAPKAppInfo(String appPath) {
    ApkMeta apkMeta;
    String versionName = "unknown";
    String versionCode = "unknown";
    String applicationName = "unknown";
    ApkFile apkFile = null;
    try {
      apkFile = new ApkFile(new File(appPath));
      apkMeta = apkFile.getApkMeta();
      versionName = apkMeta.getVersionName();
      versionCode = apkMeta.getVersionCode().toString();
      applicationName = apkMeta.getLabel();
    } catch (IOException exception) {
      LOG.error(String.format("Error reading metadata from %s", appPath), exception);
    } finally {
      try {
        if (apkFile != null) {
          apkFile.close();
        }
      } catch (IOException e) {
      }
    }

    return new ApplicationInfo(applicationName, versionName, versionCode);
  }

  private NSDictionary getDictionary(File path) {
    try {
      return (NSDictionary) PropertyListParser.parse(path);
    } catch (IOException
        | PropertyListFormatException
        | ParseException
        | ParserConfigurationException
        | SAXException exception) {
      LOG.error(String.format("Error reading dictionary from %s", path), exception);
    }
    return null;
  }

  private ApplicationInfo getAppInfoFromDictionary(NSDictionary dictionary) {
    String versionName = dictionary.objectForKey("CFBundleShortVersionString").toString();
    String versionCode = dictionary.objectForKey("CFBundleVersion").toString();
    String applicationName = dictionary.objectForKey("CFBundleDisplayName").toString();
    return new ApplicationInfo(applicationName, versionName, versionCode);
  }
}
