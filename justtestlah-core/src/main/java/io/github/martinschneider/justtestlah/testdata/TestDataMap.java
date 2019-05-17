package io.github.martinschneider.justtestlah.testdata;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

/**
 * Container to hold test data.
 *
 * <p>Test data is loaded from YAML files on the classpath which match the specified ant pattern.
 */
@Component
public class TestDataMap {

  private static final Logger LOG = LoggerFactory.getLogger(TestDataMap.class);

  private static final String TEST_DATA_YAML_PATTERN = "**/__filter__/testdata/**/*.y*ml";

  @Value("${testdata.filter}")
  private String testDataFilter;

  @Value("${model.package}")
  private String modelPackage;

  @Autowired private TestDataParser parser;

  @Autowired private TestDataObjectRegistry registry;

  private Map<Class<?>, Map<String, Object>> testDataMap =
      new HashMap<Class<?>, Map<String, Object>>();

  /**
   * Initialize the map.
   *
   * @throws IOException {@link IOException} if a test data resource cannot be processed
   */
  @PostConstruct
  public void initializeTestDataMap() throws IOException {
    LOG.info("Initialising test data map");
    initializeTestDataObjectRegistry();
    String pattern;
    if (testDataFilter != null || testDataFilter.isEmpty() || testDataFilter.startsWith("$")) {
      pattern = TEST_DATA_YAML_PATTERN.replace("__filter__", testDataFilter);
    } else {
      pattern = TEST_DATA_YAML_PATTERN.replace("__filter__", "**");
    }
    LOG.info("Scanning for test data files using the pattern {}", pattern);
    for (Resource resource : new PathMatchingResourcePatternResolver().getResources(pattern)) {
      Pair<Object, String> result = parser.parse(resource);
      Object entity = result.getLeft();
      String entityName = result.getRight();
      Class<?> type = entity.getClass();
      LOG.info("Adding {}, {} to test data map for type {}", entityName, entity, type.getName());
      if (!testDataMap.containsKey(type)) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(entityName, entity);
        testDataMap.put(type, map);
      } else {
        testDataMap.get(type).put(entityName, entity);
      }
    }
  }

  private void initializeTestDataObjectRegistry() {
    LOG.info("Initialising test data object registry");
    LOG.info("Scanning classpath for test data classes");
    try (ScanResult scanResult =
        new ClassGraph().whitelistPackages(modelPackage).enableAnnotationInfo().scan()) {
      for (ClassInfo routeClassInfo :
          scanResult.getClassesWithAnnotation(TestData.class.getName())) {
        Class<?> type = routeClassInfo.loadClass();

        String name = type.getAnnotation(TestData.class).value();
        if (name.isEmpty()) {
          name = type.getSimpleName().toLowerCase();
        }
        LOG.info("Register class {} as {}", type, name);
        registry.register(type, name);
      }
    }
  }

  public <T> T get(Class<T> type, String name) {
    return (T) testDataMap.get(type).get(name);
  }

  public <T> T get(Class<T> type) {
    return get(type, "default");
  }

  public String testdata(String name) {
    return get(String.class, name);
  }
}
