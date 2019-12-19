package qa.justtestlah.integration.steps;

import io.cucumber.java.en.Given;
import javax.mail.search.SearchTerm;
import qa.justtestlah.annotations.EntryExitLogging;
import qa.justtestlah.base.BaseSteps;
import qa.justtestlah.integration.pages.GooglePage;

public class GoogleSteps extends BaseSteps {

  private GooglePage google;

  @Given("I am on the homepage")
  @EntryExitLogging(entryExitLogLevel = EntryExitLogging.INFO)
  public void homepage() {
    test1("test1", "test2");
    test2("test1", "test2");
    test3("test1", "test2");
    test4("test1", "test2");
    test5("test1", "test2");
    google.verify();
//    testdata(SearchTerm.class);
  }
  
  @EntryExitLogging(entryExitLogLevel = EntryExitLogging.DEBUG)
  private void test1(String var1, String var2)
  {
    
  }
  
  @EntryExitLogging(entryExitLogLevel = EntryExitLogging.ERROR)
  private void test2(String var1, String var2)
  {
    
  }
  
  @EntryExitLogging(entryExitLogLevel = EntryExitLogging.OFF)
  private void test3(String var1, String var2)
  {
    
  }
  
  @EntryExitLogging(entryExitLogLevel = EntryExitLogging.TRACE)
  private void test4(String var1, String var2)
  {
    
  }
  
  @EntryExitLogging(entryExitLogLevel = EntryExitLogging.WARN)
  private void test5(String var1, String var2)
  {
    
  }
}
