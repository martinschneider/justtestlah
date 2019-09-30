package qa.justtestlah.examples.carousell.pages.web;

import static qa.justtestlah.configuration.Platform.Constants.WEB;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import qa.justtestlah.annotations.ScreenIdentifier;
import qa.justtestlah.examples.carousell.pages.HomePage;

@Component
@Profile(WEB)
@ScreenIdentifier("USER_AVATAR")
public class WebHomePage extends HomePage {}
