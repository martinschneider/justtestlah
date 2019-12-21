package pages;

import static qa.justtestlah.configuration.Platform.Constants.WEB;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import model.Search;
import qa.justtestlah.annotations.ScreenIdentifier;
import qa.justtestlah.base.BasePage;

@Component
@Profile(WEB)
@ScreenIdentifier("SEARCH_FIELD")
public class GooglePage extends BasePage<GooglePage> {

	public void search(Search searchTerm){
		$("SEARCH_FIELD").sendKeys(searchTerm.getSearchTerm());
		$("SEARCH_FIELD").pressEnter();
	}

	public boolean hasSearchResults() {
		return $("SEARCH_RESULT").isDisplayed();
	}
}
