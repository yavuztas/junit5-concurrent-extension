package dev.yavuztas.junit;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Test;

@Documented
@Target(METHOD)
@Retention(RUNTIME)
@Test
public @interface ConcurrentTest {

  /**
   * Parallel thread count, default is 10. This can be overriden by:
   * <pre>ConcurrentExtension.withGlobalThreadCount</pre>
   *
   * @return thread count
   */
  int count() default 10;

  /**
   * Set true to print extra information in the following format:
   * <pre>
   *   Thread#19 > testMethodName()
   *   Thread#20 > testMethodName()
   *   ...
   * </pre><br>
   * Default is false.
   *
   * @return true or false
   */
  boolean printInfo() default false;
}
