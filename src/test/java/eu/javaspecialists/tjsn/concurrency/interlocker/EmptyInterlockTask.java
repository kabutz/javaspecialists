package eu.javaspecialists.tjsn.concurrency.interlocker;

/**
 * Empty interlock task - it does not need locking to maintain correct count,
 * since the call() method is only ever called by a single thread at a time.
 * This is used to test the performance of the various interlockers.
 * <p/>
 * Described in http://www.javaspecialists.eu/archive/Issue188.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class EmptyInterlockTask implements
        InterlockTask<Integer> {
    public final int upto;
    private volatile int count;

    public EmptyInterlockTask(int upto) {
        this.upto = upto;
    }

    public boolean isDone() {
        return count >= upto;
    }

    public void call() {
        count++;
    }

    public Integer get() {
        return count;
    }

    public void reset() {
        count = 0;
    }
}