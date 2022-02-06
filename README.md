# junit5-concurrent-extension
Junit 5 Extension for @ConcurrentTest

An extension for Junit 5 to execute concurrency tests with ease, by using a single annotation, *@ConcurrentTest*.

A quick example:
```java
@ExtendWith(ConcurrentExtension.class)
public class ConcurrencyTest {

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
```
For a more detailed example:
- [ConcurrentTopicUpdateTest.java](#)
