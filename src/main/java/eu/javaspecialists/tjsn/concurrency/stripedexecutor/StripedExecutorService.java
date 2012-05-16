package eu.javaspecialists.tjsn.concurrency.stripedexecutor;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class StripedExecutorService extends AbstractExecutorService {
  private final ExecutorService executor;
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition terminating = lock.newCondition();

  private final Map<IdentityKey, SerialExecutor> executors =
      new HashMap<>();

  private State state = State.RUNNING;

  public StripedExecutorService(ExecutorService executor) {
    this.executor = executor;
  }

  /**
   * The default submit() method creates a new FutureTask and
   * wraps our StripedRunnable with it.  We thus need to remember
   * the stripe object somewhere.  In our case, we will do this
   * inside the ThreadLocal "stripes".
   */
  private final static ThreadLocal<Object> stripes =
      new ThreadLocal<>();

  protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
    if (isStripedObject(runnable)) {
      stripes.set(((StripedObject) runnable).getStripe());
    }
    return super.newTaskFor(runnable, value);
  }

  protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
    if (isStripedObject(callable)) {
      stripes.set(((StripedObject) callable).getStripe());
    }
    return super.newTaskFor(callable);
  }

  public Future<?> submit(Runnable task) {
    return submit(task, null);
  }

  public <T> Future<T> submit(Runnable task, T result) {
    lock.lock();
    try {
      checkPoolIsRunning();
      if (isStripedObject(task)) {
        return super.submit(task, result);
      } else { // bypass the serial executors
        return executor.submit(task, result);
      }
    } finally {
      lock.unlock();
    }
  }

  public <T> Future<T> submit(Callable<T> task) {
    lock.lock();
    try {
      checkPoolIsRunning();
      if (isStripedObject(task)) {
        return super.submit(task);
      } else { // bypass the serial executors
        return executor.submit(task);
      }
    } finally {
      lock.unlock();
    }
  }

  private void checkPoolIsRunning() {
    assert lock.isHeldByCurrentThread();
    if (state != State.RUNNING) {
      throw new RejectedExecutionException("executor not running");
    }
  }

  private static boolean isStripedObject(Object o) {
    return o instanceof StripedObject;
  }

  public void execute(Runnable command) {
    lock.lock();
    try {
      checkPoolIsRunning();
      Object stripe = getStripe(command);
      if (stripe != null) {
        IdentityKey key = new IdentityKey(stripe);
        SerialExecutor ser_exec = executors.get(key);
        if (ser_exec == null) {
          executors.put(key, ser_exec = new SerialExecutor(stripe));
          System.out.println("SerialExecutor created for " + stripe);
        }
        ser_exec.execute(command);
      } else {
        executor.execute(command);
      }
    } finally {
      lock.unlock();
    }
  }

  /**
   * We get the stripe object either from the Runnable if it also
   * implements StripedObject, or otherwise from the thread local
   * temporary storage.  Result may be null.
   */
  private Object getStripe(Runnable command) {
    Object stripe;
    if (command instanceof StripedObject) {
      stripe = (((StripedObject) command).getStripe());
    } else {
      stripe = stripes.get();
    }
    stripes.remove();
    return stripe;
  }

  public void shutdown() {
    lock.lock();
    try {
      state = State.SHUTDOWN;
      if (executors.isEmpty()) {
        executor.shutdown();
      }
    } finally {
      lock.unlock();
    }
  }

  private void removeEmptySerialExecutor(Object stripe, SerialExecutor ser_ex) {
    IdentityKey key = new IdentityKey(stripe);
    assert ser_ex == executors.get(key);
    assert lock.isHeldByCurrentThread();
    assert ser_ex.isEmpty();

    executors.remove(key);
    terminating.signalAll();
    if (state == State.SHUTDOWN && executors.isEmpty()) {
      executor.shutdown();
    }
  }

  public List<Runnable> shutdownNow() {
    lock.lock();
    try {
      shutdown();
      List<Runnable> result = new ArrayList<>();
      for (SerialExecutor ser_ex : executors.values()) {
        ser_ex.tasks.drainTo(result);
      }
      result.addAll(executor.shutdownNow());
      return result;
    } finally {
      lock.unlock();
    }
  }

  public boolean isShutdown() {
    lock.lock();
    try {
      return state == State.SHUTDOWN;
    } finally {
      lock.unlock();
    }
  }

  public boolean isTerminated() {
    lock.lock();
    try {
      if (state == State.RUNNING) return false;
      for (SerialExecutor executor : executors.values()) {
        if (!executor.isEmpty()) return false;
      }
      return executor.isTerminated();
    } finally {
      lock.unlock();
    }
  }

  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    lock.lock();
    try {
      long waitUntil = System.nanoTime() + unit.toNanos(timeout);
      long remainingTime;
      while ((remainingTime = waitUntil - System.nanoTime()) > 0 && !executors.isEmpty()) {
        terminating.awaitNanos(remainingTime);
      }
      if (remainingTime <= 0) return false;

      if (executors.isEmpty()) {
        return executor.awaitTermination(remainingTime, TimeUnit.NANOSECONDS);
      }
      return false;
    } finally {
      lock.unlock();
    }

  }

  private static boolean DEBUG = true;

  private class SerialExecutor implements Executor {
    private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
    private Runnable active;
    private final Object stripe;

    private SerialExecutor(Object stripe) {
      this.stripe = stripe;
      System.out.println("SerialExecutor created for " + stripe);
    }

    protected void finalize() throws Throwable {
      if (DEBUG) {
        System.out.println("SerialExecutor finalized for " + stripe);
        super.finalize();
      }
    }

    public void execute(final Runnable r) {
      lock.lock();
      try {
        tasks.offer(new Runnable() {
          public void run() {
            try {
              r.run();
            } finally {
              scheduleNext();
            }
          }
        });
        if (active == null) {
          scheduleNext();
        }
      } finally {
        lock.unlock();
      }
    }

    private void scheduleNext() {
      lock.lock();
      try {
        if ((active = tasks.poll()) != null) {
          executor.execute(active);
          terminating.signalAll();
        } else {
          removeEmptySerialExecutor(stripe, this);
        }
      } finally {
        lock.unlock();
      }
    }

    public boolean isEmpty() {
      lock.lock();
      try {
        return active == null && tasks.isEmpty();
      } finally {
        lock.unlock();
      }
    }
  }

  private static class IdentityKey {
    private final Object key;

    IdentityKey(Object key) {
      this.key = key;
    }

    public boolean equals(Object o) {
      if (!(o instanceof IdentityKey)) return false;
      return key == ((IdentityKey) o).key;
    }

    public int hashCode() {
      return System.identityHashCode(key);
    }
  }

  private static enum State {
    RUNNING, SHUTDOWN
  }
}