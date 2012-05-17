package eu.javaspecialists.tjsn.concurrency.interlocker.impl;

import eu.javaspecialists.tjsn.concurrency.interlocker.*;

import java.util.concurrent.atomic.*;

/**
 * This interlocker is similar to the LockFreeInterlocker, in that it uses a
 * busy loop to wait for its turn.
 * <p/>
 * Described in http://www.javaspecialists.eu/archive/Issue188.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class AtomicInterlocker extends Interlocker {
    private enum Turn {
        EVEN, ODD
    }

    private static class Job implements Runnable {
        private final InterlockTask task;
        private final AtomicReference<Turn> nextTurn;
        private final Turn turn;
        private final Turn next;

        private Job(InterlockTask task, AtomicReference<Turn> nextTurn, Turn turn) {
            this.task = task;
            this.nextTurn = nextTurn;
            this.turn = turn;
            next = Turn.values()[(turn.ordinal() + 1) % Turn.values().length];
        }

        public void run() {
            while (!task.isDone()) {
                while (!task.isDone() && nextTurn.get() != turn) {
                    // spin
                }
                if (task.isDone()) return;
                task.call();
                nextTurn.set(next);
            }
        }
    }

    protected Runnable[] getRunnables(InterlockTask task) {
        AtomicReference<Turn> nextTurn = new AtomicReference<Turn>(Turn.EVEN);
        return new Runnable[]{
                new Job(task, nextTurn, Turn.EVEN),
                new Job(task, nextTurn, Turn.ODD)
        };
    }
}
