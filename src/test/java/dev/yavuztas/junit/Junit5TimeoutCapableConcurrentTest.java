package dev.yavuztas.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Supports class/method level @Timeout annotation
 */
@Timeout(value = 5L, unit = TimeUnit.SECONDS)
@ExtendWith(ConcurrentExtension.class)
public class Junit5TimeoutCapableConcurrentTest {

  static final Object CONSTANT_VALUE = new Object();
  static final ConcurrentHashMap<String, Object> threads = new ConcurrentHashMap<>();

  /**
   * Method level @Timeout overrides class level
   */
  @ConcurrentTest(count = 2)
  void testFast() throws InterruptedException {
    String threadId = "Thread#" + Thread.currentThread().getId();
    Thread.sleep(3000);
    threads.putIfAbsent(threadId, CONSTANT_VALUE);
  }

  /**
   * Method level @Timeout overrides class level
   */
  @Timeout(value = 8L, unit = TimeUnit.SECONDS)
  @ConcurrentTest(count = 2)
  void testSlow() throws InterruptedException {
    String threadId = "Thread#" + Thread.currentThread().getId();
    Thread.sleep(6000);
    threads.putIfAbsent(threadId, CONSTANT_VALUE);
  }

  @AfterEach
  void testCount() {
    System.out.println("Threads count: " + threads.size());
    System.out.println(threads.keySet());
    assertEquals(2, threads.size());
    // clear state for the next test method
    threads.clear();
  }

}
