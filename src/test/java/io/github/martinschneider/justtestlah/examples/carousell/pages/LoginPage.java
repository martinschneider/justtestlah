package io.github.martinschneider.justtestlah.examples.carousell.pages;

import static com.codeborne.selenide.Condition.appear;

import io.github.martinschneider.justtestlah.base.BasePage;
import io.github.martinschneider.justtestlah.configuration.Platform;
import io.github.martinschneider.justtestlah.user.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({Platform.ANDROID, Platform.WEB})
public class LoginPage extends BasePage<LoginPage> {
  private HomePage home;

  /**
   * Log in the given user.
   *
   * @param user the user to log in
   * @return {@link HomePage}
   */
  public HomePage login(User user) {
    $("USERNAME_FIELD").sendKeys(user.getUsername());
    $("PASSWORD_FIELD").sendKeys(user.getPassword());
    $("LOGIN_BUTTON").click();
    return home;
  }

  public boolean isErrorMessagePresent() {
    return $("ERROR_MESSAGE").shouldBe(appear).isDisplayed();
  }
}
