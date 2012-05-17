package eu.javaspecialists.tjsn.concurrency.interlocker;

import eu.javaspecialists.tjsn.concurrency.interlocker.impl.*;
import org.junit.*;

import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests that performance of the lock-free vs blocking interlocker
 * implementations work.
 * <p/>
 * Described in http://www.javaspecialists.eu/archive/Issue188.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class EvenOddPerformanceTest {

    private final Interlocker[] executors = {
            new LockFreeInterlocker(),
            new AtomicInterlocker(),
            new ConditionInterlocker(),
            new WaitNotifyInterlocker(),
            new SemaphoreInterlocker(),
    };

    @Test
    public void testFastInterlocking() throws InterruptedException {
        // warm-up
        raceExecutors();
        raceExecutors();
        // real race
        Map<Long, Class<? extends Interlocker>> results = raceExecutors();

        for (Map.Entry<Long, Class<? extends Interlocker>> entry : results.entrySet()) {
            System.out.println(entry.getValue().getSimpleName() + " in " + entry.getKey() + "ms");
        }

        checkLockFreeAreFastest(results);
    }

    private void checkLockFreeAreFastest(Map<Long, Class<? extends Interlocker>> results) {
        Iterator<Class<? extends Interlocker>> it = results.values().iterator();
        assertTrue(lockFree(it.next()));
        assertTrue(lockFree(it.next()));
        assertFalse(lockFree(it.next()));
        assertFalse(lockFree(it.next()));
        assertFalse(lockFree(it.next()));
        assertFalse(it.hasNext());
    }

    private Map<Long, Class<? extends Interlocker>> raceExecutors() throws InterruptedException {
        Map<Long, Class<? extends Interlocker>> results = new TreeMap<>();
        for (Interlocker executor : executors) {
            InterlockTask<?> task = new EmptyInterlockTask(100 * 1000);
            long time = System.currentTimeMillis();
            executor.execute(task);
            time = System.currentTimeMillis() - time;
            results.put(time, executor.getClass());
        }
        return results;
    }

    private boolean lockFree(Class<? extends Interlocker> interlockerClass) {
        return interlockerClass == AtomicInterlocker.class || interlockerClass == LockFreeInterlocker.class;
    }
}