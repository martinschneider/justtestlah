package io.github.martinschneider.justtestlah.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import org.junit.Before;
import org.junit.Test;

public class UserServiceTest {

  private UserService target = new UserService();

  /** Initialise test users. */
  @Before
  public void init() {
    String baseFolder = this.getClass().getPackage().getName().replaceAll("\\.", File.separator);
    target.usersFile =
        this.getClass().getClassLoader().getResource(baseFolder + "/users.properties").getFile();
    target.initialize();
  }

  @Test
  public void testUserService() {
    assertThat(target.get("userKey")).as("check user map").isEqualTo(new User("user", "pw"));
  }
}
