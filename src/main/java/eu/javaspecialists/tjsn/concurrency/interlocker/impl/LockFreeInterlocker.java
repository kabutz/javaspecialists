package eu.javaspecialists.tjsn.concurrency.interlocker.impl;

import eu.javaspecialists.tjsn.concurrency.interlocker.*;

/**
 * This interlocker uses a busy wait loop that checks a shared volatile field
 * for changes.
 * <p/>
 * Described in http://www.javaspecialists.eu/archive/Issue188.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class LockFreeInterlocker extends Interlocker {
    private volatile boolean evenHasNextTurn = true;

    private class Job implements Runnable {
        private final InterlockTask task;
        private final boolean even;

        private Job(InterlockTask task, boolean even) {
            this.task = task;
            this.even = even;
        }

        public void run() {
            while (!task.isDone()) {
                while (even ^ evenHasNextTurn) {
                    // spin
                }
                if (task.isDone()) {
                    return;
                }
                task.call();
                evenHasNextTurn = !even;
            }
        }
    }

    protected Runnable[] getRunnables(InterlockTask task) {
        return new Runnable[]{
                new Job(task, true), new Job(task, false)
        };
    }
}