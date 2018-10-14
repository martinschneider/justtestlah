package io.github.martinschneider.yasew.visual;

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

  private boolean found;
  private int matchX;
  private int matchY;

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
}
