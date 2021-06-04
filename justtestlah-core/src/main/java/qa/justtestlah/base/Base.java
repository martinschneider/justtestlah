package qa.justtestlah.base;

import java.lang.reflect.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Base class for all Spring managed pages and step definitions
 *
 * <p>We inject all page objects. This way it is not necessary to annotate them with {@link
 * org.springframework.beans.factory.annotation.Autowired}.
 */
public class Base implements ApplicationContextAware, InitializingBean {

  private static final Logger LOG = LoggerFactory.getLogger(Base.class);
  private ApplicationContext applicationContext;

  @Override
  public void afterPropertiesSet() {
    initPages();
  }
  /**
   * inject the page objects (without using @org.springframework.beans.factory.annotation.Autowired
   * annotations)
   */
  @SuppressWarnings("squid:S3011")
  public void initPages() {
    LOG.info("Initializing page objects for class {}", this.getClass());
    Class<?> clazz = this.getClass();
    while (clazz != Base.class) {
      for (Field field : clazz.getDeclaredFields()) {
        if (BasePage.class.isAssignableFrom(field.getType())) {
          field.setAccessible(true);
          try {
            LOG.debug("Loading page object {} of type {}", field.getName(), field.getType());
            field.set(this, applicationContext.getBean(field.getType()));
          } catch (BeansException | IllegalArgumentException | IllegalAccessException exception) {
            LOG.error("Error initializing page objects for class {}", this.getClass());
            LOG.error("Exception", exception);
          }
        }
      }
      clazz = clazz.getSuperclass();
    }
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }
}
