package dev.yavuztas.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ConcurrentExtension.class)
public class Junit5ConcurrentTestAnnotationTest {

  static class Topic {

    private final String name;

    private final Set<String> subscriptions = new HashSet<>();

    public Topic(String name) {
      this.name = name;
    }

    public int count() {
      return this.subscriptions.size();
    }

    public void subscribe(String userId) {
      this.subscriptions.add(userId);
    }

    @Override
    public String toString() {
      return new StringJoiner(", ", Topic.class.getSimpleName() + "[", "]")
          .add("name='" + this.name + "'")
          .toString();
    }
  }

  private static final ConcurrentHashMap<String, Topic> topics = new ConcurrentHashMap<>();

  private static long time;

  @BeforeAll
  public static void setup() {
    time = System.currentTimeMillis();
  }

  @ConcurrentTest(count = 100, printInfo = true)
  public void testTopicConcurrentUpdate() {
    final String threadId = "Thread#" + Thread.currentThread().getId();
    final Topic topic = topics.computeIfAbsent(threadId, Topic::new);
    topic.subscribe(threadId);
  }

  @AfterAll
  public static void testCount() {
    System.out.println("Elapsed time: " + (System.currentTimeMillis() - time) + " ms");
    System.out.println("Topic count: " + topics.size());
    System.out.println(topics.values());
    final int total = topics.values().stream().mapToInt(Topic::count).sum();
    assertEquals(100, total);
  }

}
