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
   * Can be overriden by:
   * <pre>ConcurrentExtension.withGlobalThreadCount</pre>
   */
  int count() default 10;

  boolean printInfo() default false;
}
