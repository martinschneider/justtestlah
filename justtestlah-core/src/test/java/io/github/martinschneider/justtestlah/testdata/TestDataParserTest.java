package io.github.martinschneider.justtestlah.testdata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

public class TestDataParserTest {
  private TestDataParser target = new TestDataParser();

  @Test
  public void testParser() throws IOException {
    TestDataObjectRegistry testDataObjectRegistry = new TestDataObjectRegistry();
    testDataObjectRegistry.register(TestEntity1.class, "entity1");
    target.setYamlParser(new Yaml());
    target.setTestDataObjectRegistry(testDataObjectRegistry);
    Pair<Object, String> result =
        target.parse(new ClassPathResource("TestEntity1.yaml", this.getClass()));
    assertThat(result.getRight()).as("check entity name").isEqualTo("TestEntity1");
    Object element = result.getLeft();
    assertThat(element.getClass()).as("check type of element").isEqualTo(TestEntity1.class);
    TestEntity1 entity1 = (TestEntity1) element;
    assertThat(entity1.getValue()).as("check value").isEqualTo("test");
  }

  @Test
  public void testPrimitiveTypes() throws IOException {
    TestDataObjectRegistry testDataObjectRegistry = new TestDataObjectRegistry();
    testDataObjectRegistry.register(TestEntity2.class, "entity2");
    target.setYamlParser(new Yaml());
    target.setTestDataObjectRegistry(testDataObjectRegistry);
    Pair<Object, String> result =
        target.parse(new ClassPathResource("TestEntity2.yaml", this.getClass()));
    assertThat(result.getRight()).as("check entity name").isEqualTo("TestEntity2");
    Object element = result.getLeft();
    assertThat(element.getClass()).as("check type of element").isEqualTo(TestEntity2.class);
    TestEntity2 entity2 = (TestEntity2) element;
    assertThat(entity2.getStringValue()).as("check String value").isEqualTo("000");
    assertThat(entity2.getIntValue()).as("check int value").isEqualTo(1);
    assertThat(entity2.getDoubleValue()).as("check double value").isEqualTo(2.0);
    assertThat(entity2.isBooleanValue()).as("check boolean value").isEqualTo(true);
  }

  @Test
  public void testMissingTestDataFile() throws IOException {
    TestDataObjectRegistry testDataObjectRegistry = new TestDataObjectRegistry();
    target.setYamlParser(new Yaml());
    target.setTestDataObjectRegistry(testDataObjectRegistry);
    assertThat(
            assertThrows(
                    TestDataException.class,
                    () -> target.parse(new ClassPathResource("404.yaml")),
                    "Expected exception")
                .getMessage())
        .as("check exception message")
        .isEqualTo("Test data YAML file class path resource [404.yaml] could not be loaded");
  }

  @Test
  public void testMultipleEntitiesInTestDatafile() throws IOException {
    TestDataObjectRegistry testDataObjectRegistry = new TestDataObjectRegistry();
    target.setYamlParser(new Yaml());
    target.setTestDataObjectRegistry(testDataObjectRegistry);
    assertThat(
            assertThrows(
                    TestDataException.class,
                    () ->
                        target.parse(
                            new ClassPathResource("MultipleEntities.yaml", this.getClass())),
                    "Expected exception")
                .getMessage())
        .as("check exception message")
        .isEqualTo(
            "The YAML test data file must contain exactly one root node, found 2: [Entity1, Entity2]");
  }

  @Test
  public void testMissingTestDataEntity() throws IOException {
    TestDataObjectRegistry testDataObjectRegistry = new TestDataObjectRegistry();
    target.setYamlParser(new Yaml());
    target.setTestDataObjectRegistry(testDataObjectRegistry);
    assertThat(
            assertThrows(
                    TestDataException.class,
                    () -> target.parse(new ClassPathResource("TestEntity1.yaml", this.getClass())),
                    "Expected exception")
                .getMessage())
        .as("check exception message")
        .isEqualTo("No test data class registered for key: entity1");
  }
}
