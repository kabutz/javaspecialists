package eu.javaspecialists.tjsn.examples.issue188;

import java.util.*;

/**
 * Whilst writing the Interlocker, we discovered this bug in the JVM server
 * hotspot that has been around since 1.6.0_14.  The JVM might end up in an
 * unbreakable hard spin if you run this code.  It has been logged as a bug.
 * <p/>
 * Described in http://www.javaspecialists.eu/archive/Issue188.html
 *
 * @author Dr Heinz M. Kabutz
 */

public class VirtualMachineLiveLock {
    private static final boolean BROKEN = true;

    public volatile boolean evenHasNextTurn = true;

    private class InterleavedTask implements Runnable {
        private final boolean even;

        InterleavedTask(boolean even) {
            this.even = even;
        }

        public void run() {
            while (!isDone()) {
                // busy wait - tends to hang up the process.
                while (BROKEN ?
                        even && !evenHasNextTurn || !even && evenHasNextTurn
                        : even ^ evenHasNextTurn) ;
                if (isDone()) {
                    return;
                }
                call();
                evenHasNextTurn = !even;
            }
        }
    }

    private final int upto = 50;
    private volatile int count;

    public boolean isDone() {
        return count >= upto;
    }

    public void call() {
        int temp = count + 1;
        sleepQuietly();
        count = temp;
        System.out.println("temp = " + temp);
    }

    private void sleepQuietly() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            public void run() {
                System.out.println("VM still alive (a bit)");
            }
        }, 1000, 1000);

        VirtualMachineLiveLock vmll = new VirtualMachineLiveLock();
        vmll.check();
    }

    private void check() throws InterruptedException {
        Thread[] threads = {
                new Thread(new InterleavedTask(true)),
                new Thread(new InterleavedTask(false)),
        };
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }
}