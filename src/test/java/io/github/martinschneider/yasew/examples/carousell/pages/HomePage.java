package io.github.martinschneider.yasew.examples.carousell.pages;

import static com.codeborne.selenide.Condition.appear;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import io.github.martinschneider.yasew.base.BasePage;
import io.github.martinschneider.yasew.configuration.Platform;

@Component
@Profile({Platform.ANDROID, Platform.IOS})
public class HomePage extends BasePage<HomePage> {

  private LoginPage loginPage;

  public LoginPage goToLogin() {
    $("LOGIN_BUTTON").click();
    return loginPage;
  }

  public boolean isUserMenuVisible() {
    return $("USER_MENU").shouldBe(appear).isDisplayed();
  }
}
