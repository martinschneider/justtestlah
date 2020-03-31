package qa.justtestlah.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/** Helper class to provide access to Spring beans in classes not managed by Spring. */
@Component
public class SpringContext implements ApplicationContextAware {

  private static ApplicationContext context;

  /**
   * Returns the Spring managed bean instance of the given class type if it exists. Returns null
   * otherwise.
   *
   * @param <T> type of the requested bean
   * @param clazz the {@link Class} of the requested bean
   * @return matching bean from the Spring context
   */
  public static <T extends Object> T getBean(Class<T> clazz) {
    return context.getBean(clazz);
  }

  @Override
  public void setApplicationContext(ApplicationContext context) throws BeansException {
    SpringContext.context = context;
  }
}
