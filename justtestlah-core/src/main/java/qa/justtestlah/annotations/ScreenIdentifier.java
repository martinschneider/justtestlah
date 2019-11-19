package qa.justtestlah.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to identify locators that need to be present on a page/screen.
 *
 * <p>See {@link qa.justtestlah.base.BasePage#verify(int)}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ScreenIdentifier {
  String[] value();
}
