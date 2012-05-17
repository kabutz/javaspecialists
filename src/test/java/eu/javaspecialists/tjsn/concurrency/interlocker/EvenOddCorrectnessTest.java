package eu.javaspecialists.tjsn.concurrency.interlocker;

import eu.javaspecialists.tjsn.concurrency.interlocker.impl.*;
import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests that the various interlocker implementations work as expected.
 * <p/>
 * Described in http://www.javaspecialists.eu/archive/Issue188.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class EvenOddCorrectnessTest {

    private final Interlocker[] executors = {
            new LockFreeInterlocker(),
            new AtomicInterlocker(),
            new ConditionInterlocker(),
            new WaitNotifyInterlocker(),
            new SemaphoreInterlocker(),
    };

    @Test
    public void testInterlockedPrinting() throws InterruptedException {
        for (Interlocker executor : executors) {
            check(5, executor, new ThreadPrintingTask(5));
        }
    }

    @Test
    public void testDifferentNumbersOfInterlocking() throws InterruptedException {
        int[] uptos = {
                0, -1, Integer.MIN_VALUE, 1, 2, 3, 4, 5, 11, 12, 1000,
        };
        for (int upto : uptos) {
            for (Interlocker executor : executors) {
                check(upto, executor, new EmptyInterlockTask(upto));
            }
        }
    }

    @Test
    public void testInterlockingCalls() throws InterruptedException {
        int upto = 1000;
        for (Interlocker executor : executors) {
            InterlockTask<VerifyResult> task = new InterleavedNumberTestingStrategy(upto);
            System.out.printf("Testing %s(%d) with %s%n",
                    task.getClass().getSimpleName(), upto,
                    executor.getClass().getSimpleName());
            VerifyResult result = executor.execute(task);
            assertTrue(result.isSuccess());
        }
    }

    @Test
    public void testForRaceConditionsWithSleep() throws InterruptedException {
        for (int i = 0; i < 4; i++) {
            int upto = (int) (Math.random() * 10) + 40;
            for (Interlocker executor : executors) {
                check(upto, executor, new RaceConditionTestingStrategy(upto));
            }
        }
    }

    private void check(int upto, Interlocker executor, InterlockTask<Integer> task) throws InterruptedException {
        System.out.printf("Testing %s(%d) with %s%n",
                task.getClass().getSimpleName(), upto,
                executor.getClass().getSimpleName());
        executor.execute(task);
        int result = task.get();
        assertEquals(Math.max(0, upto), result);
    }
}