package io.github.martinschneider.justtestlah.visual;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import javax.imageio.ImageIO;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Utility methods for image processing. */
public class ImageUtils {

  private static final Logger LOG = LoggerFactory.getLogger(ImageUtils.class);

  private static final String IMAGE_FOLDER = "images";

  /**
   * @param imageName the name of a test image (as used in the locator yaml files)
   * @return a {@link File} representing the specified image
   */
  public File getImageAsFile(String imageName) {
    return new File(getFullPath(imageName));
  }

  /**
   * @param imageName the name of a test image (as used in the locator yaml files)
   * @return a Base64 encoded {@link String} representing the specified image
   */
  public String getImageAsBase64String(String imageName) {
    return encodeBase64(getFullPath(imageName));
  }

  /**
   * @param imageName the name of a test image (as used in the locator yaml files)
   * @return the fully-qualified path to the specified image
   */
  public String getFullPath(String imageName) {
    return this.getClass().getClassLoader().getResource(IMAGE_FOLDER + "/" + imageName).getFile();
  }

  /**
   * @param fullPath fully-qualified path to an image file
   * @return a Base64 encoded {@link String} representing the specified image
   */
  public String encodeBase64(String fullPath) {
    try {
      return Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(fullPath)));
    } catch (IOException exception) {
      LOG.error(String.format("Error reading image %s", fullPath), exception);
      return null;
    }
  }

  /**
   * @param image a {@link BufferedImage}
   * @return a Base64 encoded {@link String} representing the specified image
   */
  public byte[] imageToBase64String(BufferedImage image) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      ImageIO.write(image, "png", baos);
    } catch (IOException e) {
      LOG.error("Error processing image");
      return null;
    }
    return Base64.getEncoder().encode(baos.toByteArray());
  }

  /**
   * @param image the {@link BufferedImage} to scale
   * @param scaleFactor the scale factor (&lt;1 will scale down, &gt;1 will scale up, 1 = 100%)
   * @return a {@link BufferedImage} which is a version of the original image scaled by the given
   *     factor
   */
  public BufferedImage scaleImage(BufferedImage image, double scaleFactor) {
    int newWidth = (int) (image.getWidth() * scaleFactor);
    int newHeight = (int) (image.getHeight() * scaleFactor);
    BufferedImage outputImage = new BufferedImage(newWidth, newHeight, image.getType());
    Graphics2D g2d = outputImage.createGraphics();
    g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
    g2d.dispose();
    image = outputImage;
    return image;
  }

  /**
   * @param image the {@link Mat} to scale
   * @param scaleFactor the scale factor (&lt;1 will scale down, &gt;1 will scale up, 1 = 100%)
   * @return a {@link Mat} which is a version of the original image scaled by the given factor
   */
  public Mat scaleImage(Mat image, double scaleFactor) {
    Mat resizedImage = new Mat();
    Size sz = new Size(image.width() * scaleFactor, image.height() * scaleFactor);
    Imgproc.resize(image, resizedImage, sz);
    return resizedImage;
  }
}
