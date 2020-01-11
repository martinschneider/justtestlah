package qa.justtestlah.testdata;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class TestDataObjectRegistryTest {
  private TestDataObjectRegistry target = new TestDataObjectRegistry();

  @Test
  public void testRegister() {
    target.register(TestEntity1.class, "entity1");
    assertThat(target.size()).as("check size of registry").isEqualTo(1);
    assertThat(target.get("entity1")).isEqualTo(TestEntity1.class);
  }

  @Test
  public void testAddingExistinKey() {
    target.register(TestEntity1.class, "entity1");
    target.register(TestEntity2.class, "entity1");
    assertThat(target.size()).as("check size of registry").isEqualTo(1);
    assertThat(target.get("entity1")).isEqualTo(TestEntity1.class);
  }

  @Test
  public void testAddingExistingClass() {
    target.register(TestEntity1.class, "entity1");
    target.register(TestEntity1.class, "entity2");
    assertThat(target.size()).as("check size of registry").isEqualTo(1);
    assertThat(target.get("entity1")).isEqualTo(TestEntity1.class);
    assertThat(target.get("entity2")).isNull();
  }

  @Test
  public void testAddingExistingentry() {
    target.register(TestEntity1.class, "entity1");
    target.register(TestEntity1.class, "entity1");
    assertThat(target.size()).as("check size of registry").isEqualTo(1);
    assertThat(target.get("entity1")).isEqualTo(TestEntity1.class);
  }
}
