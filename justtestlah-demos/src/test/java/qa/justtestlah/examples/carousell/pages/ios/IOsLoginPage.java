package qa.justtestlah.examples.carousell.pages.ios;

import static com.codeborne.selenide.Condition.appear;
import static qa.justtestlah.configuration.Platform.Constants.IOS;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import qa.justtestlah.examples.carousell.model.User;
import qa.justtestlah.examples.carousell.pages.HomePage;
import qa.justtestlah.examples.carousell.pages.LoginPage;

@Component
@Profile(IOS)
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
