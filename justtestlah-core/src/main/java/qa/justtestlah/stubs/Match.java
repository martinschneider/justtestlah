package qa.justtestlah.stubs;

public interface Match {
  boolean isFound();

  void setFound(boolean found);

  int getX();

  void setX(int matchX);

  int getY();

  void setY(int matchY);

  byte[] getVisualization();

  void setVisualization(byte[] visualization);
}
