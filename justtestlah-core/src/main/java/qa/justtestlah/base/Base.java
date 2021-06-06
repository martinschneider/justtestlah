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
   * Recursively inject the page objects (without
   * using @org.springframework.beans.factory.annotation.Autowired annotations)
   */
  @SuppressWarnings("squid:S3011")
  public synchronized void initPages() {
    LOG.info("Initializing page objects for class {}", this);
    Class<?> clazz = this.getClass();
    while (clazz != Base.class) {
      for (Field field : clazz.getDeclaredFields()) {
        if (BasePage.class.isAssignableFrom(field.getType())) {
          field.setAccessible(true);
          try {
            LOG.debug("Loading page object {} of type {}", field.getName(), field.getType());
            Object bean = applicationContext.getBean(field.getType());
            if (bean != null) {
              field.set(this, bean);
              ((Base) bean).initPages();
            } else {
              LOG.error(
                  "Couldn't inject non-existing page {} into {}. Skipping!",
                  field.getType().getSimpleName(),
                  this.getClass().getSimpleName());
            }
          } catch (BeansException | IllegalArgumentException | IllegalAccessException exception) {
            LOG.error(
                String.format("Error initializing page objects for class {}", this.getClass()),
                exception);
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
