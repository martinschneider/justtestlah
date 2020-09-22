package qa.justtestlah.visual;

import io.appium.java_client.imagecomparison.OccurrenceMatchingResult;
import org.openqa.selenium.Rectangle;

/**
 * Class representing a match.
 *
 * <p>found = true, if a match has been detected
 *
 * <p>if found = true then matchX and matchY represent the rounded coordinates of the center of the
 * match
 */
public class Match implements qa.justtestlah.stubs.Match {

  private Rectangle match;
  private byte[] visualization;

  /**
   * Constructor.
   *
   * @param match {@link Rectangle} representing the location and size of the match
   */
  public Match(Rectangle match) {
    super();
    this.match = match;
  }

  /**
   * Constructor.
   *
   * @param match {@link Rectangle} representing the match, null if not found
   * @param visualization a Base64 encoded visualization image of the match
   */
  public Match(Rectangle match, byte[] visualization) {
    super();
    this.match = match;
    this.visualization = visualization;
  }

  @Override
  public boolean isFound() {
    return match != null;
  }

  @Override
  public byte[] getVisualization() {
    return visualization;
  }

  @Override
  public void setVisualization(byte[] visualization) {
    this.visualization = visualization;
  }

  /**
   * Map a {@link OccurrenceMatchingResult} to a {@link Match}
   *
   * @param found has a match been found
   * @param result the {@link OccurrenceMatchingResult}
   * @param scalingFactor by how much has the image been scaled in regards to the original image (if
   *     a {@link Match} in regards to the scaled image is required this should be set to 1.0)
   * @return {@link Match}
   */
  public static Match fromOccurrenceMatchingResult(
      boolean found, OccurrenceMatchingResult result, double scalingFactor) {
    if (result == null) {
      return new Match(null);
    }
    return new Match(result.getRect(), result.getVisualization());
  }

  /**
   * Map a {@link OccurrenceMatchingResult} to a {@link Match}
   *
   * @param found has a match been found
   * @param result the {@link OccurrenceMatchingResult}
   * @return {@link Match}
   */
  public static Match fromOccurrenceMatchingResult(boolean found, OccurrenceMatchingResult result) {
    return fromOccurrenceMatchingResult(found, result, 1.0);
  }

  @Override
  public Rectangle getRect() {
    return match;
  }
}
