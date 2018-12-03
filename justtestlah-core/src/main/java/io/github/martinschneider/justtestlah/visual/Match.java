package io.github.martinschneider.justtestlah.visual;

import io.appium.java_client.imagecomparison.OccurrenceMatchingResult;

/**
 * Class representing a match.
 *
 * <p>found = true, if a match has been detected
 *
 * <p>if found = true then matchX and matchY represent the rounded coordinates of the center of the
 * match
 */
public class Match {

  /**
   * Constructor.
   *
   * @param found true, if a match has been detected
   */
  public Match(boolean found) {
    super();
    this.found = found;
  }

  /**
   * Constructor.
   *
   * @param found true, if a match has been detected
   * @param matchX x coordinate of the center of the match
   * @param matchY y coordinate of the center of the match
   */
  public Match(boolean found, int matchX, int matchY) {
    super();
    this.found = found;
    this.matchX = matchX;
    this.matchY = matchY;
  }

  /**
   * Constructor.
   *
   * @param found true, if a match has been detected
   * @param matchX x coordinate of the center of the match
   * @param matchY y coordinate of the center of the match
   * @param visualization a Base64 encoded visualization image of the match
   */
  public Match(boolean found, int matchX, int matchY, byte[] visualization) {
    super();
    this.found = found;
    this.matchX = matchX;
    this.matchY = matchY;
    this.visualization = visualization;
  }

  private boolean found;
  private int matchX;
  private int matchY;
  private byte[] visualization;

  public boolean isFound() {
    return found;
  }

  public void setFound(boolean found) {
    this.found = found;
  }

  public int getX() {
    return matchX;
  }

  public void setX(int matchX) {
    this.matchX = matchX;
  }

  public int getY() {
    return matchY;
  }

  public void setY(int matchY) {
    this.matchY = matchY;
  }

  public byte[] getVisualization() {
    return visualization;
  }

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
   * @return {@link} Match
   */
  public static Match fromOccurrenceMatchingResult(
      boolean found, OccurrenceMatchingResult result, double scalingFactor) {
    if (!found) {
      return new Match(false, 0, 0, null);
    }
    return new Match(
        true,
        (int)
            Math.round(
                (result.getRect().getX() + result.getRect().getHeight() / 2.0) * scalingFactor),
        (int)
            Math.round(
                (result.getRect().getY() + result.getRect().getWidth() / 2.0) * scalingFactor),
        result.getVisualization());
  }

  /**
   * Map a {@link OccurrenceMatchingResult} to a {@link Match}
   *
   * @param found has a match been found
   * @param result the {@link OccurrenceMatchingResult}
   * @return {@link} Match
   */
  public static Match fromOccurrenceMatchingResult(boolean found, OccurrenceMatchingResult result) {
    return fromOccurrenceMatchingResult(found, result, 1.0);
  }
}
