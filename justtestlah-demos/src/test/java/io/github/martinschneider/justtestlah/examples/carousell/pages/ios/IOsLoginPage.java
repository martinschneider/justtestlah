package io.github.martinschneider.justtestlah.examples.carousell.pages.ios;

import static com.codeborne.selenide.Selenide.sleep;

import io.github.martinschneider.justtestlah.configuration.Platform;
import io.github.martinschneider.justtestlah.examples.carousell.pages.HomePage;
import io.github.martinschneider.justtestlah.examples.carousell.pages.LoginPage;
import io.github.martinschneider.justtestlah.user.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Platform.IOS)
public class IOsLoginPage extends LoginPage {

  private HomePage home;

  @Override
  public HomePage login(User user) {
    super.login(user);
    sleep(5000);
    $("CONFIRM_CHAT_ALERTS").click();
    $("ALLOW_NOTIFICATIONS").click();
    return home;
  }
}
