package qa.justtestlah.stubs;

import org.openqa.selenium.Rectangle;

public interface Match {
  boolean isFound();

  Rectangle getRect();

  byte[] getVisualization();

  void setVisualization(byte[] visualization);
}
