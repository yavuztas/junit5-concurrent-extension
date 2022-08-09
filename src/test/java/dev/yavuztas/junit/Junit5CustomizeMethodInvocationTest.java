package dev.yavuztas.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.yavuztas.junit.Junit5CustomizeMethodInvocationTest.CustomConcurrentExtension;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

@ExtendWith(CustomConcurrentExtension.class)
public class Junit5CustomizeMethodInvocationTest {

  static final Object CONSTANT_VALUE = new Object();
  static final ConcurrentHashMap<Long, Object> threads = new ConcurrentHashMap<>();

  static class CustomConcurrentExtension extends ConcurrentExtension {

    public CustomConcurrentExtension() {
      // set default concurrency level
      this.globalThreadCount = 16;
    }

    static void logWrapper(Runnable command) {
      System.out.println("Running thread#" + Thread.currentThread().getId() + " ...");
      command.run();
      System.out.println("thread#" + Thread.currentThread().getId() + " done.");
    }

    @Override
    protected void invokeTestMethod(ReflectiveInvocationContext<Method> invocationContext) {
      // wrap test method invocation with some custom logging
      logWrapper(() -> {
        super.invokeTestMethod(invocationContext);
      });
    }
  }

  @ConcurrentTest
  void testConcurrency() {
    long threadId = Thread.currentThread().getId();
    threads.putIfAbsent(threadId, CONSTANT_VALUE);
  }

  @AfterEach
  void testCount() {
    assertEquals(16, threads.size());
  }

}
