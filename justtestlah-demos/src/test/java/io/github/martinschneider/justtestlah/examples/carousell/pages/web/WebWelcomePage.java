package io.github.martinschneider.justtestlah.examples.carousell.pages.web;

import static com.codeborne.selenide.Selenide.open;

import io.github.martinschneider.justtestlah.configuration.Platform;
import io.github.martinschneider.justtestlah.examples.carousell.pages.LoginPage;
import io.github.martinschneider.justtestlah.examples.carousell.pages.WelcomePage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Platform.WEB)
public class WebWelcomePage extends WelcomePage {

  private LoginPage login;

  @Override
  public LoginPage goToLogin() {
    open(configuration.getBaseUrl());
    super.goToLogin();
    return login;
  }
}
