package io.github.martinschneider.justtestlah.examples.carousell.pages.ios;

import static com.codeborne.selenide.Condition.appear;

import io.github.martinschneider.justtestlah.configuration.Platform;
import io.github.martinschneider.justtestlah.examples.carousell.model.User;
import io.github.martinschneider.justtestlah.examples.carousell.pages.HomePage;
import io.github.martinschneider.justtestlah.examples.carousell.pages.LoginPage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Platform.IOS)
public class IOsLoginPage extends LoginPage {

  private HomePage home;

  @Override
  public HomePage login(User user) {
    super.login(user);
    $("CONFIRM_CHAT_ALERTS").should(appear).click();
    $("ALLOW_NOTIFICATIONS").should(appear).click();
    return home;
  }
}
