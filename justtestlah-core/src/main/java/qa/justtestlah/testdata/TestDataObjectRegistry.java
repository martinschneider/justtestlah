package qa.justtestlah.testdata;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Register Java classes which can act as test data entities. */
@Component
public class TestDataObjectRegistry {

  private static final Logger LOG = LoggerFactory.getLogger(TestDataObjectRegistry.class);

  private BiMap<String, Class<?>> registry = Maps.synchronizedBiMap(HashBiMap.create());

  public void register(Class<?> type, String name) {
    // keys must be unique in both directions
    if (registry.containsKey(name)) {
      if (registry.get(name).equals(type)) {
        LOG.info(
            "Test data object {}: {} already present, skipping.",
            name,
            registry.get(name).getName());
      } else {
        LOG.warn(
            "There is already a test data object registered for key {}: {}. Not adding new value {}!",
            name,
            registry.get(name).getName(),
            type.getName());
      }
    } else if (registry.containsValue(type)) {
      LOG.warn(
          "There is already a test data object registered for type {}: {}. Not adding new value {}!",
          type.getName(),
          registry.inverse().get(type),
          name);
    } else {
      registry.put(name, type);
    }
  }

  public Class<?> get(String key) {
    return registry.get(key);
  }

  public int size() {
    return registry.size();
  }
}
