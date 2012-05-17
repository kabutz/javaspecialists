package eu.javaspecialists.tjsn.concurrency.interlocker.impl;

import eu.javaspecialists.tjsn.concurrency.interlocker.*;

/**
 * This interlocker uses the classical wait/notify approach for passing the
 * baton between threads.
 * <p/>
 * Described in http://www.javaspecialists.eu/archive/Issue188.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class WaitNotifyInterlocker extends Interlocker {
    private boolean evenHasNextTurn = true;
    private final Object lock = new Object();

    private class Job implements Runnable {
        private final InterlockTask task;
        private final boolean even;

        Job(InterlockTask task, boolean even) {
            this.task = task;
            this.even = even;
        }

        public void run() {
            try {
                while (!task.isDone()) {
                    synchronized (lock) {
                        while (!task.isDone() && (even ^ evenHasNextTurn)) {
                            lock.wait();
                        }
                        if (task.isDone()) return;
                        task.call();
                        evenHasNextTurn = !evenHasNextTurn;
                        lock.notify();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    protected Runnable[] getRunnables(InterlockTask task) {
        return new Runnable[]{
                new Job(task, true),
                new Job(task, false)
        };
    }
}
