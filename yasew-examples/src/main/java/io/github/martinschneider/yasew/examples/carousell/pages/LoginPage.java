package io.github.martinschneider.yasew.examples.carousell.pages;

import static com.codeborne.selenide.Condition.appear;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import io.github.martinschneider.yasew.base.BasePage;
import io.github.martinschneider.yasew.configuration.Platform;
import io.github.martinschneider.yasew.user.User;

@Component
@Profile({Platform.ANDROID, Platform.WEB})
public class LoginPage extends BasePage<LoginPage> {
  private HomePage home;

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
