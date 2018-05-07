package io.github.martinschneider.yasew.examples.carousell.pages.ios;

import io.github.martinschneider.yasew.configuration.Platform;
import io.github.martinschneider.yasew.examples.carousell.pages.HomePage;
import io.github.martinschneider.yasew.examples.carousell.pages.LoginPage;
import io.github.martinschneider.yasew.user.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Platform.IOS)
public class IOsLoginPage extends LoginPage {

  private HomePage home;

  @Override
  public HomePage login(User user) {
    super.login(user);
    $("CONFIRM_CHAT_ALERTS").click();
    $("ALLOW_NOTIFICATIONS").click();
    return home;
  }
}
