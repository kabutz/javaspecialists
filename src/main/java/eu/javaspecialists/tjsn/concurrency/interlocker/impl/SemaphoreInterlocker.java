package eu.javaspecialists.tjsn.concurrency.interlocker.impl;

import eu.javaspecialists.tjsn.concurrency.interlocker.*;

import java.util.concurrent.*;

/**
 * The easiest solution is using Semaphores.  It can also be expanded to work
 * with more than two threads.
 * <p/>
 * Described in http://www.javaspecialists.eu/archive/Issue188.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class SemaphoreInterlocker extends Interlocker {
    private static class Job implements Runnable {
        private final InterlockTask task;
        private final Semaphore first;
        private final Semaphore second;

        public Job(InterlockTask task,
                   Semaphore first, Semaphore second) {
            this.task = task;
            this.first = first;
            this.second = second;
        }

        public void run() {
            while (!task.isDone()) {
                first.acquireUninterruptibly();
                if (task.isDone()) return;
                task.call();
                second.release();
            }
        }
    }

    protected Runnable[] getRunnables(InterlockTask task) {
        Semaphore even = new Semaphore(1);
        Semaphore odd = new Semaphore(0);
        return new Runnable[]{
                new Job(task, even, odd),
                new Job(task, odd, even)
        };
    }
}