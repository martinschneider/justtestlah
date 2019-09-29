package qa.justtestlah.examples.carousell.pages;

import static qa.justtestlah.configuration.Platform.Constants.ANDROID;
import static qa.justtestlah.configuration.Platform.Constants.IOS;
import static qa.justtestlah.configuration.Platform.Constants.WEB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import qa.justtestlah.annotations.ScreenIdentifier;
import qa.justtestlah.base.BasePage;

@Component
@Profile({ANDROID, IOS, WEB})
@ScreenIdentifier("LOGIN_BUTTON")
public class WelcomePage extends BasePage<WelcomePage> {

  @Autowired private LoginPage loginPage;

  public LoginPage goToLogin() {
    $("LOGIN_BUTTON").click();
    return loginPage;
  }
}
