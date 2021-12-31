package qa.justtestlah.examples.google.pages;

import static qa.justtestlah.configuration.Platform.Constants.WEB;

import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import qa.justtestlah.annotations.ScreenIdentifier;
import qa.justtestlah.base.BasePage;
import qa.justtestlah.examples.google.model.Search;
import qa.justtestlah.visual.OCR;

@Component
@Profile(WEB)
@ScreenIdentifier("SEARCH_FIELD")
public class GooglePage extends BasePage<GooglePage> {

  @Autowired private OCR ocr;

  private ResultsPage results;

  public ResultsPage search(Search searchTerm) {
    $("SEARCH_FIELD").sendKeys(searchTerm.getSearchTerm());
    $("SEARCH_FIELD").pressEnter();
    return results;
  }

  public String getLogoText() {
    return ocr.getText($("LOGO"));
  }

  public WebElement getLogo() {
    return $("LOGO_IMG");
  }
}
