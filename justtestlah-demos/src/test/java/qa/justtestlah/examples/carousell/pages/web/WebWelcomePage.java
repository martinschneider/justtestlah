package qa.justtestlah.examples.carousell.pages.web;

import static com.codeborne.selenide.Selenide.open;
import static qa.justtestlah.configuration.Platform.Constants.WEB;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import qa.justtestlah.examples.carousell.pages.LoginPage;
import qa.justtestlah.examples.carousell.pages.WelcomePage;

@Component
@Profile(WEB)
public class WebWelcomePage extends WelcomePage {

  private LoginPage login;

  @Override
  public LoginPage goToLogin() {
    open(configuration.getBaseUrl());
    super.goToLogin();
    return login;
  }
}
