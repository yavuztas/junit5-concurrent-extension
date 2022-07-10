package dev.yavuztas.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.RegisterExtension;

public class Junit5DynamicExtensionTest {

  static int parallelThreads = Integer.parseInt(System.getProperty("thread.count", "8"));

  /**
   * Supports @RegisterExtension to dynamically set thread count for all @ConcurrentTest methods.
   * This setting overrides annotation level thread count setting.
   */
  @RegisterExtension
  static ConcurrentExtension extension = ConcurrentExtension
      .withGlobalThreadCount(parallelThreads);

  static final Object CONSTANT_VALUE = new Object();
  static final ConcurrentHashMap<String, Object> threads = new ConcurrentHashMap<>();

  @BeforeEach
  void setup() {
    threads.clear();
  }

  @ConcurrentTest
  void testConcurrency() {
    String threadId = "Thread#" + Thread.currentThread().getId();
    threads.putIfAbsent(threadId, CONSTANT_VALUE);
  }

  @Tag("limited")
  @ConcurrentTest(count = 2, overrideGlobal = true)
  void testConcurrencyOverrideGlobal() {
    String threadId = "Thread#" + Thread.currentThread().getId();
    threads.putIfAbsent(threadId, CONSTANT_VALUE);
  }

  @AfterEach
  void testCount(TestInfo testInfo) {
    System.out.println("Threads count: " + threads.size());
    System.out.println(threads.keySet());

    // for tagged "limited" tests thread count to validate is 2
    if (testInfo.getTags().contains("limited")) {
      assertEquals(2, threads.size());
    } else {
      assertEquals(parallelThreads, threads.size());
    }
  }

}
