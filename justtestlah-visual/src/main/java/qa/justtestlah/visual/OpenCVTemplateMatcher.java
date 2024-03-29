package qa.justtestlah.visual;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.openqa.selenium.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import qa.justtestlah.configuration.JustTestLahConfiguration;
import qa.justtestlah.stubs.Match;
import qa.justtestlah.stubs.TemplateMatcher;

/**
 * Implementation of {@link TemplateMatcher} using OpenCV on the client
 *
 * <p>This class provides methods to check whether a given image (template) is part of another one
 * (target). We use a simple (yet effective) way to detect the template image in various sizes by
 * scaling the target up and down to a minimum and maximum size.
 *
 * <p>We return on the first match that exceeds the specific threshold (matching quality). This
 * means that it is not necessarily the best possible match.
 */
@Component
@Primary
public class OpenCVTemplateMatcher implements TemplateMatcher {

  private static final Logger LOG = LoggerFactory.getLogger(OpenCVTemplateMatcher.class);

  private JustTestLahConfiguration configuration;

  /*
   * (non-Javadoc)
   *
   * @see qa.justtestlah.visual.TemplateMatcherI#match(java.lang.String,
   * java.lang.String, double)
   */
  @Override
  public Match match(String targetFile, String templateFile, double threshold) {
    return match(
        targetFile,
        templateFile,
        threshold,
        LocalDateTime.now().format(DateTimeFormatter.ofPattern(Constants.DATE_PATTERN)));
  }

  /*
   * (non-Javadoc)
   *
   * @see qa.justtestlah.visual.TemplateMatcherI#match(java.lang.String,
   * java.lang.String, double, java.lang.String)
   */
  @Override
  public Match match(String targetFile, String templateFile, double threshold, String description) {
    checkForOpenCv();
    Mat image = Imgcodecs.imread(targetFile);
    Mat templ = Imgcodecs.imread(templateFile);
    MinMaxLocResult bestMatch = new MinMaxLocResult();
    Mat originalImage = image;
    /**
     * This could be improved by combining the two while loops (checking the original size first,
     * then slightly smaller and larger images etc. instead of first scaling all the way down and
     * then scaling up (or by using a framework that performs size-invariant template matching).
     */
    while (image.width() > Constants.MIN_IMAGE_WIDTH) {
      int resultCols = image.cols() - templ.cols() + 1;
      int resultRows = image.rows() - templ.rows() + 1;
      if (resultCols < 0 || resultRows < 0) {
        break;
      }
      Mat result = new Mat(resultRows, resultCols, CvType.CV_32FC1);
      Imgproc.matchTemplate(image, templ, result, Imgproc.TM_CCOEFF_NORMED);
      MinMaxLocResult match = Core.minMaxLoc(result);
      if (match.maxVal > bestMatch.maxVal) {
        bestMatch = match;
      }
      if (bestMatch.maxVal >= threshold) {
        break;
      }
      // else
      LOG.debug(
          "Image {} scaled to {}x{} does not contain {} with match quality>={}",
          new File(targetFile).getName(),
          image.cols(),
          image.rows(),
          new File(templateFile).getName(),
          threshold);
      image = ImageUtils.scaleImage(image, 0.9);
    }
    if (bestMatch.maxVal < threshold) {
      image = originalImage;
    }
    while (bestMatch.maxVal < threshold && image.width() < Constants.MAX_IMAGE_WIDTH) {
      int resultCols = image.cols() - templ.cols() + 1;
      int resultRows = image.rows() - templ.rows() + 1;
      Mat result = new Mat(resultRows, resultCols, CvType.CV_32FC1);
      Imgproc.matchTemplate(image, templ, result, Imgproc.TM_CCOEFF_NORMED);
      MinMaxLocResult match = Core.minMaxLoc(result);
      if (match.maxVal > bestMatch.maxVal) {
        bestMatch = match;
      }
      if (match.maxVal >= threshold) {
        break;
      }
      // else
      LOG.debug(
          "Image {} scaled to {}x{} does not contain {} with match quality>={}",
          new File(targetFile).getName(),
          image.cols(),
          image.rows(),
          new File(templateFile).getName(),
          threshold);
      image = ImageUtils.scaleImage(image, 1.1);
    }
    if (bestMatch.maxVal >= threshold) {
      // calculating the scale factor to mark the match on the original image
      double scalingFactor = (double) originalImage.width() / image.width();
      if (Math.abs(scalingFactor - 1.0) > 0.0001) {
        LOG.info(
            "Image {} scaled to {}x{} contains image {} with match quality {} at coordinates ({},{})",
            new File(targetFile).getName(),
            image.cols(),
            image.rows(),
            new File(templateFile).getName(),
            bestMatch.maxVal,
            (int) Math.round(bestMatch.maxLoc.x + templ.cols() / 2.0),
            (int) Math.round(bestMatch.maxLoc.y + templ.rows() / 2.0));
      }

      double x1 = bestMatch.maxLoc.x * scalingFactor;
      double x2 = (bestMatch.maxLoc.x + templ.cols()) * scalingFactor;
      double y1 = bestMatch.maxLoc.y * scalingFactor;
      double y2 = (bestMatch.maxLoc.y + templ.rows()) * scalingFactor;

      LOG.info(
          "Original image {} with size {}x{} contains image {} with match quality {} at coordinates ({},{})",
          new File(targetFile).getName(),
          originalImage.cols(),
          originalImage.rows(),
          new File(templateFile).getName(),
          bestMatch.maxVal,
          (int) Math.round((bestMatch.maxLoc.x + templ.cols() / 2.0) * scalingFactor),
          (int) Math.round((bestMatch.maxLoc.y + templ.rows() / 2.0) * scalingFactor));

      Imgproc.rectangle(
          originalImage, new Point(x1, y1), new Point(x2, y2), new Scalar(255, 0, 0), 5);
      String fileName =
          System.getProperty("user.dir")
              + File.separator
              + "target"
              + File.separator
              + description
              + "."
              + Constants.FILE_EXTENSION;
      LOG.info("Writing visualization of template matching to {}", fileName);
      Imgcodecs.imwrite(fileName, originalImage);

      return new qa.justtestlah.visual.Match(
          new Rectangle(
              (int) Math.round(bestMatch.maxLoc.y * scalingFactor),
              (int) Math.round(bestMatch.maxLoc.x * scalingFactor),
              (int) Math.round(templ.rows() * scalingFactor),
              (int) Math.round(templ.cols() * scalingFactor)));
    }
    // else
    LOG.info(
        "Image {} does not contain image {} with match quality>={}. The closest match has quality {}",
        new File(targetFile).getName(),
        new File(templateFile).getName(),
        threshold,
        bestMatch.maxVal);
    return new qa.justtestlah.visual.Match(null);
  }

  @Autowired
  public void setConfiguration(JustTestLahConfiguration configuration) {
    this.configuration = configuration;
  }

  /** Check whether OpenCV is enabled. */
  private void checkForOpenCv() {
    if (!configuration.isOpenCvEnabled()) {
      throw new UnsupportedOperationException(
          "OpenCV is not enabled. Set opencv.enabled=true in justtestlah.properties!");
    }
  }
}
