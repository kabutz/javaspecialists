package eu.javaspecialists.tjsn.concurrency.interlocker;

/**
 * This special executor guarantees that the call() method of the task parameter
 * is invoked in turns by two threads.
 * <p/>
 * Described in http://www.javaspecialists.eu/archive/Issue188.html
 *
 * @author Dr Heinz M. Kabutz
 */
public abstract class Interlocker {
    protected abstract Runnable[] getRunnables(InterlockTask task);

    public final <T> T execute(InterlockTask<T> task)
            throws InterruptedException {
        Runnable[] jobs = getRunnables(task);
        Thread[] threads = new Thread[jobs.length];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(jobs[i]);
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        return task.get();
    }
}