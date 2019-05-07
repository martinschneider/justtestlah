package io.github.martinschneider.justtestlah.user;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/** Simple service to load test users from properties file. */
public final class UserService {
  private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

  public static final String TESTUSERS_DEFAULT_FILE = "testusers.properties";

  @Value("${testusers.file}")
  String usersFile;

  private Map<String, User> users = new HashMap<String, User>();

  /**
   * Initialise the user service. Load test users from users.properties file defined in
   * justtestlah.properties.
   */
  public void initialize() {
    LOG.info("Loading test user definitions from {}", usersFile);
    users = new HashMap<String, User>();
    Properties props = new Properties();
    try {
      if (usersFile != null && !usersFile.isEmpty()) {
        props.load(new FileInputStream(usersFile));
      } else {
        LOG.info("Loading JustTestLah properties from classpath ({})", TESTUSERS_DEFAULT_FILE);
        props.load(UserService.class.getClassLoader().getResourceAsStream(TESTUSERS_DEFAULT_FILE));
      }
    } catch (IOException exception) {
      LOG.warn("Error loading test user definitions from {}", usersFile);
    }
    for (final String key : props.stringPropertyNames()) {
      String[] userValues = props.getProperty(key).split(":");
      users.put(key, new User(userValues[0], userValues[1]));
    }
  }

  /**
   * Retrieves a value from the user map.
   *
   * @param key key to retrieve
   * @return value for the given key
   */
  public User get(String key) {
    User user = users.get(key);
    if (user == null) {
      throw new RuntimeException(String.format("User %s not found", key));
    }
    return user;
  }

  public void setUsersFile(String usersFile) {
    this.usersFile = usersFile;
  }
}
