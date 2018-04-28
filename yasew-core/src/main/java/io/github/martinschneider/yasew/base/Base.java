package io.github.martinschneider.yasew.base;

import java.lang.reflect.Field;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import io.github.martinschneider.yasew.configuration.SpringContext;

/**
 * Base class for all Spring managed pages and step definitions
 *
 * <p>We inject all page objects. This way it is not necessary to annotate them with {@link
 * org.springframework.beans.factory.annotation.Autowired}.
 */
@ContextConfiguration(classes = SpringContext.class)
public class Base implements ApplicationContextAware {

  private Logger LOG = LoggerFactory.getLogger(Base.class);
  private ApplicationContext applicationContext;

  /**
   * inject the page objects (without using @org.springframework.beans.factory.annotation.Autowired
   * annotations)
   */
  @PostConstruct
  public void initPages() {
    LOG.info("Initializing page objects for class {}", this.getClass());
    for (Field field : this.getClass().getDeclaredFields()) {
      if (BasePage.class.isAssignableFrom(field.getType())) {
        field.setAccessible(true);
        try {
          LOG.debug("Loading page object {} of type {}", field.getName(), field.getClass());
          field.set(this, applicationContext.getBean(field.getType()));
        } catch (BeansException | IllegalArgumentException | IllegalAccessException e) {
          LOG.error("Error initializing page objects for class {}", this.getClass());
          LOG.error("Exception", e);
        }
      }
    }
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }
}
