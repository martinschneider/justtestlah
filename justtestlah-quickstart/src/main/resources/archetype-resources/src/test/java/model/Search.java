package model;

import qa.justtestlah.testdata.TestData;

@TestData("search")
public class Search {
  private String searchTerm;

  public String getSearchTerm() {
    return searchTerm;
  }

  public void setSearchTerm(String searchTerm) {
    this.searchTerm = searchTerm;
  }
}
