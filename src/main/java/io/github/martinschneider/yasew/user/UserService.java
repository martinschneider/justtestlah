package io.github.martinschneider.yasew.user;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/** Simple service to load test users from properties file */
public final class UserService {
  private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

  private static final String TESTUSERS_DEFAULT_FILE = "users.properties";

  @Value("${testusers.file:" + TESTUSERS_DEFAULT_FILE + "}")
  String usersFile;

  private Map<String, User> users = new HashMap<String, User>();

  public void initialize() {
    LOG.info("Loading test user definitions from {}", usersFile);
    users = new HashMap<String, User>();
    Properties props = new Properties();
    try {
      props.load(new FileInputStream(usersFile));
    } catch (IOException e) {
      LOG.warn("Error loading test user definitions from {}", usersFile);
    }
    for (final String key : props.stringPropertyNames()) {
      String[] userValues = props.getProperty(key).split(":");
      users.put(key, new User(userValues[0], userValues[1]));
    }
  }

  /**
   * Retrieves a value from the message store
   *
   * @param key key to retrieve
   * @return value for the given key
   */
  public User get(String key) {
    return users.get(key);
  }
}
