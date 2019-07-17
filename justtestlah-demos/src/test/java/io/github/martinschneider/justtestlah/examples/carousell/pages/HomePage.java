package io.github.martinschneider.justtestlah.examples.carousell.pages;

import static com.codeborne.selenide.Condition.appear;

import io.github.martinschneider.justtestlah.base.BasePage;
import io.github.martinschneider.justtestlah.configuration.Platform;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({Platform.ANDROID, Platform.IOS, Platform.WEB})
public class HomePage extends BasePage<HomePage> {

  public boolean isSellButtonVisible() {
    return $("SELL_BUTTON").waitUntil(appear, 10000).isDisplayed();
  }
}
