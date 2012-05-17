package eu.javaspecialists.tjsn.concurrency.interlocker;

import java.util.concurrent.atomic.*;

/**
 * Prints the thread name that is invoking the call(), to allow us to manually
 * check that the interlocker is working as expected.
 * <p/>
 * Described in http://www.javaspecialists.eu/archive/Issue188.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class ThreadPrintingTask implements InterlockTask<Integer> {
    private final int upto;
    private AtomicInteger row = new AtomicInteger(0);

    public ThreadPrintingTask(int upto) {
        this.upto = upto;
    }

    public boolean isDone() {
        return row.get() >= upto;
    }

    public void call() {
        System.out.println(Thread.currentThread().getName());
        row.incrementAndGet();
    }

    public Integer get() {
        return upto;
    }

    public void reset() {
        row.set(0);
    }
}
