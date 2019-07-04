package io.github.martinschneider.justtestlah.testdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

/** Parses the test data from the YAML file into a corresponding Java object. */
@Component
public class TestDataParser {

  private static final Logger LOG = LoggerFactory.getLogger(TestDataParser.class);

  private Yaml yamlParser;

  private TestDataObjectRegistry registry;

  @Autowired
  public void setYamlParser(Yaml yamlParser) {
    this.yamlParser = yamlParser;
  }

  @Autowired
  public void setTestDataObjectRegistry(TestDataObjectRegistry registry) {
    this.registry = registry;
  }

  /**
   * Parse locators from file.
   *
   * @param resource the resource to parse the locators from
   * @return {@link Pair} the mapped Java object and the name of the entity (derived from the
   *     filename)
   */
  public Pair<Object, String> parse(Resource resource) {
    LOG.debug("Parsing test data from {}", resource.getDescription());
    InputStream inputStream = null;
    try {
      inputStream = resource.getInputStream();
    } catch (IOException exception) {
      throw new TestDataException(
          String.format("Test data YAML file %s could not be loaded", resource.getDescription()));
    }
    return map(yamlParser.load(inputStream), getEntityName(resource.getFilename()));
  }

  private Pair<Object, String> map(Map<String, Map<String, String>> input, String entityName) {
    Set<String> top = input.keySet();
    if (top.size() != 1) {
      throw new TestDataException(
          String.format(
              "The YAML test data file must contain exactly one root node, found %d: %s",
              top.size(), top));
    }
    String key = top.iterator().next();
    Class<?> type = registry.get(key);
    if (type == null) {
      throw new TestDataException(String.format("No test data class registered for key: %s", key));
    }
    return Pair.of(new ObjectMapper().convertValue(input.get(key), type), entityName);
  }

  private String getEntityName(String fileName) {
    return fileName.split("\\.")[0];
  }
}
