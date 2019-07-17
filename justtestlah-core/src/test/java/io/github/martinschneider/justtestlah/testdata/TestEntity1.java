package io.github.martinschneider.justtestlah.testdata;

@TestData("entity1")
public class TestEntity1 {

  public TestEntity1() {}

  public TestEntity1(String value) {
    super();
    this.value = value;
  }

  private String value;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
