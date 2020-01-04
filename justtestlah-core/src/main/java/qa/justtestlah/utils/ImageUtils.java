package qa.justtestlah.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Utility methods for image processing. */
@Component
public class ImageUtils {

  private static final Logger LOG = LoggerFactory.getLogger(ImageUtils.class);

  protected static final String IMAGE_FOLDER = "images";

  /**
   * @param imageName the name of a test image (as used in the locator yaml files)
   * @return a Base64 encoded {@link String} representing the specified image
   */
  public static String getImageAsBase64String(String imageName) {
    return encodeBase64(getFullPath(imageName));
  }

  /**
   * @param imageName the name of a test image (as used in the locator yaml files)
   * @return the fully-qualified path to the specified image
   */
  public static String getFullPath(String imageName) {
    return ImageUtils.class.getClassLoader().getResource(IMAGE_FOLDER + "/" + imageName).getFile();
  }

  /**
   * @param fullPath fully-qualified path to an image file
   * @return a Base64 encoded {@link String} representing the specified image
   */
  public static String encodeBase64(String fullPath) {
    try {
      return Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(fullPath)));
    } catch (IOException exception) {
      LOG.error(String.format("Error reading image %s", fullPath), exception);
      return null;
    }
  }
}
