package qa.justtestlah.examples.google.pages;

import static qa.justtestlah.configuration.Platform.Constants.WEB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import qa.justtestlah.annotations.ScreenIdentifier;
import qa.justtestlah.base.BasePage;
import qa.justtestlah.examples.google.model.Search;
import qa.justtestlah.visual.OCR;

@Component
@Profile(WEB)
@ScreenIdentifier({"SEARCH_RESULT", "RESULT_STATS"})
public class ResultsPage extends BasePage<ResultsPage> {
}
