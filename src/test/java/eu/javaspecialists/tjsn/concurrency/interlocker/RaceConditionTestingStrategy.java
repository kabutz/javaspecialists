package eu.javaspecialists.tjsn.concurrency.interlocker;

/**
 * Tests whether call() is executed the correct number of times without causing
 * a race condition.  We sleep for a short while to force a context switch.
 * <p/>
 * Described in http://www.javaspecialists.eu/archive/Issue188.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class RaceConditionTestingStrategy implements InterlockTask<Integer> {
    private final int upto;
    private volatile int count;

    public RaceConditionTestingStrategy(int upto) {
        this.upto = upto;
    }

    public boolean isDone() {
        return count >= upto;
    }

    public void call() {
        int temp = count + 1;
        sleepQuietly();
        count = temp;
    }

    private void sleepQuietly() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Integer get() {
        return count;
    }

    public void reset() {
        count = 0;
    }
}