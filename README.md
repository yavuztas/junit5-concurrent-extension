# junit5-concurrent-extension
Junit 5 Extension for *@ConcurrentTest*

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
See other examples:
- [Junit5DynamicExtensionTest.java](https://github.com/yavuztas/junit5-concurrent-extension/blob/master/src/test/java/dev/yavuztas/junit/Junit5DynamicExtensionTest.java)
- [Junit5TimeoutCapableConcurrentTest.java](https://github.com/yavuztas/junit5-concurrent-extension/blob/master/src/test/java/dev/yavuztas/junit/Junit5TimeoutCapableConcurrentTest.java)
- [ConcurrentTopicUpdateTest.java](https://github.com/yavuztas/junit5-concurrent-extension/blob/master/src/test/java/dev/yavuztas/junit/ConcurrentTopicUpdateTest.java)

### Motivation
Junit5's *ExecutionMode.CONCURRENT* currently doesn't support running a test method multiple times, ideally, amount of parallel thread count.

We can of course configure parallel executions via *junit-platform.properties*. However, each test method is executed only once. 
Combining *@RepeatedTest* into it is also not a desired solution, each repetition independently triggers *@BeforeEach*, and *@AfterEach* phases 
making it harder to manage shared state between threads. Putting all the logic in a single method is indeed a solution but causes a lot of duplications.   

### Implementation Notes
Please notice that this extension is a workaround. 

Since Junit5's invocation design does not allow to execute methods concurrently, we skip the original Junit's invocation by intention and execute the method manually in a separate thread pool. It might not work properly with the combination of other Junit extensions or annotations. 

See the implementation details in [ConcurrentExtension.java](https://github.com/yavuztas/junit5-concurrent-extension/blob/master/src/main/java/dev/yavuztas/junit/ConcurrentExtension.java)

### Java Version
This library supports Java version >= 8.
