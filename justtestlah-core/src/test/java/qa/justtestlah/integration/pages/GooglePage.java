package qa.justtestlah.integration.pages;

import static qa.justtestlah.configuration.Platform.Constants.WEB;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import qa.justtestlah.annotations.ScreenIdentifier;
import qa.justtestlah.base.BasePage;

@Component
@Profile(WEB)
@ScreenIdentifier("SEARCH_FIELD")
public class GooglePage extends BasePage<GooglePage> {}
