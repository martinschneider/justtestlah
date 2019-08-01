package qa.justtestlah.examples.carousell.pages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import qa.justtestlah.base.BasePage;
import qa.justtestlah.configuration.Platform;

@Component
@Profile({Platform.ANDROID, Platform.IOS})
public class WelcomePage extends BasePage<WelcomePage> {

  @Autowired private LoginPage loginPage;

  public LoginPage goToLogin() {
    $("LOGIN_BUTTON").click();
    return loginPage;
  }
}
