package qa.justtestlah.examples.carousell.pages;

import static com.codeborne.selenide.Condition.appear;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import qa.justtestlah.base.BasePage;
import qa.justtestlah.configuration.Platform;

@Component
@Profile({Platform.ANDROID, Platform.IOS, Platform.WEB})
public class HomePage extends BasePage<HomePage> {

  public boolean isSellButtonVisible() {
    return $("SELL_BUTTON").waitUntil(appear, 10000).isDisplayed();
  }
}
