package qa.justtestlah.testdata;

@TestData("entity1")
public class TestEntity1 {

	  private String value;
	
  public TestEntity1() {}

  public TestEntity1(String value) {
    super();
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
