package qa.justtestlah.integration.model;

import qa.justtestlah.testdata.TestData;

@TestData("searchTerm")
public class SearchTerm {
  private String searchTerm;

  public String getSearchTerm() {
    return searchTerm;
  }

  public void setSearchTerm(String searchTerm) {
    this.searchTerm = searchTerm;
  }
}
