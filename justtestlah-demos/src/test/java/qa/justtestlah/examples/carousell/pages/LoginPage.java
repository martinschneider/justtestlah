package qa.justtestlah.examples.carousell.pages;

import static com.codeborne.selenide.Condition.appear;
import static qa.justtestlah.configuration.Platform.Constants.ANDROID;
import static qa.justtestlah.configuration.Platform.Constants.WEB;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import qa.justtestlah.annotations.ScreenIdentifier;
import qa.justtestlah.base.BasePage;
import qa.justtestlah.examples.carousell.model.User;

@Component
@Profile({ANDROID, WEB})
@ScreenIdentifier({"LOGIN_BUTTON", "USERNAME_FIELD", "PASSWORD_FIELD"})
public class LoginPage extends BasePage<LoginPage> {
  private HomePage home;

  /**
   * Log in the given user.
   *
   * @param user the user to log in
   * @return {@link HomePage}
   */
  public HomePage login(User user) {
    $("USERNAME_FIELD").sendKeys(user.getUsername());
    $("PASSWORD_FIELD").sendKeys(user.getPassword());
    $("LOGIN_BUTTON").click();
    return home;
  }

  public boolean isErrorMessageVisible() {
    return $("ERROR_MESSAGE").should(appear).isDisplayed();
  }
}
