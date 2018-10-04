package io.github.martinschneider.yasew.visual;

/**
 * Class representing a match.
 *
 * <p>found = true, if a match has been detected
 *
 * <p>if found = true then x and y represent the rounded coordinates of the center of the match
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
   * @param x x coordinate of the center of the match
   * @param y y coordinate of the center of the match
   */
  public Match(boolean found, int x, int y) {
    super();
    this.found = found;
    this.x = x;
    this.y = y;
  }

  private boolean found;
  private int x;
  private int y;

  public boolean isFound() {
    return found;
  }

  public void setFound(boolean found) {
    this.found = found;
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }
}
