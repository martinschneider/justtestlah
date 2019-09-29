package qa.justtestlah.examples.carousell.pages;

import static com.codeborne.selenide.Condition.appear;
import static qa.justtestlah.configuration.Platform.Constants.ANDROID;
import static qa.justtestlah.configuration.Platform.Constants.IOS;
import static qa.justtestlah.configuration.Platform.Constants.WEB;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import qa.justtestlah.annotations.ScreenIdentifier;
import qa.justtestlah.base.BasePage;

@Component
@Profile({ANDROID, IOS, WEB})
@ScreenIdentifier("SELL_BUTTON")
public class HomePage extends BasePage<HomePage> {

  public boolean isSellButtonVisible() {
    return $("SELL_BUTTON").waitUntil(appear, 10000).isDisplayed();
  }
}
