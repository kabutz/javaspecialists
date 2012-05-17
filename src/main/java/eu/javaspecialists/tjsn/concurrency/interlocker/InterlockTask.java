package eu.javaspecialists.tjsn.concurrency.interlocker;

/**
 * Is called by two threads alternatively until isDone() returns true.
 * <p/>
 * Described in http://www.javaspecialists.eu/archive/Issue188.html
 *
 * @author Dr Heinz M. Kabutz
 */
public interface InterlockTask<T> {
    boolean isDone();

    /**
     * The call() method is called interleaved by the the threads in a
     * round-robin fashion.
     */
    void call();

    /**
     * Returns the result after all the call()'s have completed.
     */
    T get();

    void reset();
}