package qa.justtestlah.examples.carousell.pages.web;

import static com.codeborne.selenide.Selenide.open;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import qa.justtestlah.configuration.Platform;
import qa.justtestlah.examples.carousell.pages.LoginPage;
import qa.justtestlah.examples.carousell.pages.WelcomePage;

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
