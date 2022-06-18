package dev.yavuztas.junit;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.platform.commons.util.AnnotationUtils;
import org.junit.platform.commons.util.ClassUtils;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.commons.util.ReflectionUtils;

public class ConcurrentExtension implements InvocationInterceptor {

  private int globalThreadCount;

  /**
   * Overrides @{@link ConcurrentTest} threadCount parameter globally.
   *
   * @param threadCount a positive number
   */
  public static ConcurrentExtension withGlobalThreadCount(int threadCount) {
    final ConcurrentExtension instance = new ConcurrentExtension();
    instance.globalThreadCount = threadCount;
    return instance;
  }

  @Override
  public void interceptTestMethod(
      Invocation<Void> invocation,
      ReflectiveInvocationContext<Method> invocationContext,
      ExtensionContext extensionContext) throws Throwable {

    final Method testMethod = invocationContext.getExecutable();
    final Optional<ConcurrentTest> annotation =
        AnnotationUtils.findAnnotation(testMethod, ConcurrentTest.class);
    if (!annotation.isPresent()) {
      invocation.proceed();
      return;
    }

    final ConcurrentTest concurrentTest = annotation.get();
    final Throwable[] exception = new Throwable[1];
    final int threadCount = threadCount(concurrentTest, testMethod);
    final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    for (int i = 0; i < threadCount; i++) {
      CompletableFuture.runAsync(() -> {
        try {
          if (concurrentTest.printInfo()) {
            printInfo(testMethod);
          }
          ReflectionUtils.invokeMethod(
              testMethod,
              invocationContext.getTarget().orElse(null),
              invocationContext.getArguments().toArray()
          );
        } catch (Throwable t) {
          exception[0] = t;
        }
      }, executorService);
    }
    awaitTerminationAfterShutdown(executorService,
        timeout(invocationContext.getTargetClass(), testMethod));

    if (exception[0] != null) {
      throw exception[0];
    }

    // skip the junit invocation because we manually invoked the method
    invocation.skip();
  }

  private void awaitTerminationAfterShutdown(ExecutorService threadPool, Timeout timeout) {
    threadPool.shutdown();
    try {
      if (!threadPool.awaitTermination(
          timeout != null ? timeout.value() : Long.MAX_VALUE,
          timeout != null ? timeout.unit() : TimeUnit.NANOSECONDS)) {
        threadPool.shutdownNow();
      }
    } catch (InterruptedException ex) {
      threadPool.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  private void printInfo(Method testMethod) {
    final String message = String.format("Thread#%s > %s(%s)",
        Thread.currentThread().getId(),
        testMethod.getName(),
        ClassUtils.nullSafeToString(Class::getSimpleName, testMethod.getParameterTypes())
    );
    System.out.println(message);
  }

  private int threadCount(ConcurrentTest concurrent, Method method) {
    final int count = concurrent.count();
    Preconditions.condition(count > 0, () -> String.format(
        "Configuration error: @ConcurrentTest on method [%s] must be declared with a positive 'count'.",
        method));
    return this.globalThreadCount > 0 ? this.globalThreadCount : count;
  }

  private Timeout timeout(Class<?> clazz, Method method) {
    final Optional<Timeout> methodTimeout = AnnotationUtils
        .findAnnotation(method, Timeout.class);
    if (!methodTimeout.isPresent()) {
      // global timeout on class level or null
      return AnnotationUtils.findAnnotation(clazz, Timeout.class).orElse(null);
    }
    return methodTimeout.get();
  }

}
