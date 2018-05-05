package io.github.martinschneider.yasew.user;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;

public class UserServiceTest {

  private UserService target = new UserService();

  @Before
  public void init() {
    String baseFolder = this.getClass().getPackage().getName().replaceAll("\\.", "/");
    target.usersFile =
        this.getClass().getClassLoader().getResource(baseFolder + "/user.properties").getFile();
    target.initialize();
  }

  @Test
  public void testUserService() {
    assertThat(target.get("userKey")).as("check user map").isEqualTo(new User("user", "pw"));
  }
}
