package qa.justtestlah.examples.google.model;

import qa.justtestlah.testdata.TestData;

@TestData
public class Search {
  private String searchTerm;

  public String getSearchTerm() {
    return searchTerm;
  }

  public void setSearchTerm(String searchTerm) {
    this.searchTerm = searchTerm;
  }

  @Override
  public String toString() {
    return "Search [searchTerm=" + searchTerm + "]";
  }
}
