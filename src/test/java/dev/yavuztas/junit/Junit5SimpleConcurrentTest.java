package dev.yavuztas.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ConcurrentExtension.class)
public class Junit5SimpleConcurrentTest {

  static final Object CONSTANT_VALUE = new Object();
  static final ConcurrentHashMap<String, Object> threads = new ConcurrentHashMap<>();

  @ConcurrentTest(count = 10)
  void testConcurrency() {
    String threadId = "Thread#" + Thread.currentThread().getId();
    threads.putIfAbsent(threadId, CONSTANT_VALUE);
  }

  @AfterEach
  void testCount() {
    System.out.println("Threads count: " + threads.size());
    System.out.println(threads.keySet());
    assertEquals(10, threads.size());
  }

}
