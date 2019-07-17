package io.github.martinschneider.justtestlah.examples.carousell.pages;

import io.github.martinschneider.justtestlah.base.BasePage;
import io.github.martinschneider.justtestlah.configuration.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({Platform.ANDROID, Platform.IOS})
public class WelcomePage extends BasePage<WelcomePage> {

  @Autowired private LoginPage loginPage;

  public LoginPage goToLogin() {
    $("LOGIN_BUTTON").click();
    return loginPage;
  }
}
