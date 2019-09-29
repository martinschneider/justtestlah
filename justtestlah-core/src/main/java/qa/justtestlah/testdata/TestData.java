package qa.justtestlah.testdata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates Java classes representing test data entities.
 *
 * <p>All annotated classes will be registered with {@link
 * qa.justtestlah.testdata.TestDataObjectRegistry}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestData {
  /**
   * The identifier used in the testdata yaml file. If not specified the simple name of the
   * annotated class will be used.
   *
   * @return key to identify this test data class
   */
  String value() default "";
}
