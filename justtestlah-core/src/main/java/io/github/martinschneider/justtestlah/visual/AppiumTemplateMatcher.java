package io.github.martinschneider.justtestlah.visual;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.imagecomparison.OccurrenceMatchingOptions;
import io.appium.java_client.imagecomparison.OccurrenceMatchingResult;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link TemplateMatcher} using Appium's image locator (which uses OpenCV on the
 * appium server)
 *
 * <p>This class provides methods to check whether a given image (template) is part of another one
 * (target). We use a simple (yet effective) way to detect the template image in various sizes by
 * scaling the target up and down to a minimum and maximum size.
 */
@Component
public class AppiumTemplateMatcher implements TemplateMatcher {

  private static final Logger LOG = LoggerFactory.getLogger(AppiumTemplateMatcher.class);

  @Autowired private ImageUtils imageUtils;

  private static final String TEMPLATE_MATCHING_ERROR =
      "Cannot find any occurences of the partial image in the full image";

  private AppiumDriver<WebElement> driver;

  /** @param imageUtils {@link ImageUtils} */
  @Autowired
  public AppiumTemplateMatcher(ImageUtils imageUtils) {
    this.imageUtils = imageUtils;
  }

  @SuppressWarnings("unchecked")
  public void setDriver(WebDriver driver) {
    this.driver = (AppiumDriver) driver;
  }

  @Override
  public Match match(String targetFile, String templateFile, double threshold) {
    return match(
        targetFile,
        templateFile,
        threshold,
        LocalDateTime.now().format(DateTimeFormatter.ofPattern(TemplateMatcher.DATE_PATTERN)));
  }

  private Match match(
      BufferedImage originalImage,
      BufferedImage targetImage,
      String targetFile,
      String templateFile,
      byte[] target,
      byte[] template,
      double threshold) {
    boolean found = false;
    OccurrenceMatchingResult result = null;
    try {
      result =
          driver.findImageOccurrence(
              target, template, new OccurrenceMatchingOptions().withEnabledVisualization());
      found = true;
      LOG.info(
          "Image {} scaled to {}x{} contains {} at coordinates ({},{})",
          new File(targetFile).getName(),
          targetImage.getWidth(),
          targetImage.getHeight(),
          new File(templateFile).getName(),
          (int) Math.round(result.getRect().getX() + result.getRect().getHeight() / 2.0),
          (int) Math.round(result.getRect().getY() + result.getRect().getWidth() / 2.0));
    } catch (WebDriverException exception) {
      // I am not sure if this is really the only way to handle a non-match. This is terribly ugly
      // (and will fail with every change of the error message on the server side)!
      if (exception.getMessage().contains(TEMPLATE_MATCHING_ERROR)) {
        found = false;
        LOG.info(
            "Image {} scaled to {}x{} does not contain {} with match quality>={}",
            new File(targetFile).getName(),
            targetImage.getWidth(),
            targetImage.getHeight(),
            new File(templateFile).getName(),
            threshold);
      }
    }
    return Match.fromOccurrenceMatchingResult(
        found, result, (double) originalImage.getWidth() / targetImage.getWidth());
  }

  @Override
  public Match match(String targetFile, String templateFile, double threshold, String description) {
    Match match = new Match(false, 0, 0);
    byte[] template, target;
    BufferedImage targetImage, originalImage;
    try {
      template = imageUtils.encodeBase64(templateFile).getBytes();
      target = imageUtils.encodeBase64(targetFile).getBytes();
      targetImage = originalImage = ImageIO.read(new File(targetFile));
    } catch (IOException exception) {
      LOG.error("Error processing target and/or template file", exception);
      return null;
    }
    if (driver instanceof AppiumDriver) {
      while (!match.isFound() && targetImage.getWidth() > MIN_IMAGE_WIDTH) {
        match =
            match(
                originalImage, targetImage, targetFile, templateFile, target, template, threshold);
        targetImage = imageUtils.scaleImage(targetImage, 0.9);
        target = imageUtils.imageToBase64String(targetImage);
      }
      targetImage = originalImage;
      while (!match.isFound() && targetImage.getWidth() < MAX_IMAGE_WIDTH) {
        match =
            match(
                originalImage, targetImage, targetFile, templateFile, target, template, threshold);
        targetImage = imageUtils.scaleImage(targetImage, 1.1);
        target = imageUtils.imageToBase64String(targetImage);
      }
    } else {
      throw new UnsupportedOperationException(
          "This operation is not supported for the current WebDriver: "
              + driver.getClass().getSimpleName()
              + ".");
    }
    if (match.isFound()) {
      LOG.info(
          "Original image {} with size {}x{} contains image {} at coordinates ({},{})",
          new File(targetFile).getName(),
          originalImage.getWidth(),
          originalImage.getHeight(),
          new File(templateFile).getName(),
          match.getX(),
          match.getY());

      String fileName =
          System.getProperty("user.dir") + "/target/" + description + "." + FILE_EXTENSION;
      LOG.info("Writing visualization of template matching to {}", fileName);
      try {
        Files.write(Paths.get(fileName), Base64.decodeBase64(match.getVisualization()));
      } catch (IOException exception) {
        LOG.error("Error writing visualization of template matching", exception);
      }
    } else {
      LOG.info(
          "Image {} does not contain {} with match quality>={}",
          new File(targetFile).getName(),
          new File(templateFile).getName(),
          threshold);
    }
    return match;
  }
}
