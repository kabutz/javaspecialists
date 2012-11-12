package eu.javaspecialists.tjsn.examples.issue206;

import eu.javaspecialists.tjsn.concurrency.util.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class SerialExecutorDemo {
    private final static int NUMBER_OF_SERIAL_EXECUTORS = 5;
    private final static AtomicIntegerArray orders =
            new AtomicIntegerArray(NUMBER_OF_SERIAL_EXECUTORS);
    private static final int UPTO = 1000;

    private static class OrderTester implements Runnable {
        private final int ordersIndex;
        private final int expectedOrder;

        private OrderTester(int ordersIndex, int expectedOrder) {
            this.ordersIndex = ordersIndex;
            this.expectedOrder = expectedOrder;
        }

        public void run() {
            int order = orders.getAndIncrement(ordersIndex);
            if (order != expectedOrder) {
                System.err.println("Out of order - was " + order + " but " +
                        "expected " + expectedOrder);
            } else {
                System.out.printf("%d,%d is good%n", ordersIndex,
                        expectedOrder);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService pool = Executors.newCachedThreadPool();

        SerialExecutor[] executors =
                new SerialExecutor[NUMBER_OF_SERIAL_EXECUTORS];
        for (int i = 0; i < executors.length; i++) {
            executors[i] = new SerialExecutor(pool);
        }

        for (int i = 0; i < UPTO; i++) {
            for (int j = 0; j < executors.length; j++) {
                executors[j].execute(
                        new OrderTester(j, i)
                );
            }
        }

        // There is no way to easily shut down these SerialExecutors
        while (!done()) {
            Thread.sleep(10);
        }
        pool.shutdown();
        System.out.println("pool = " + pool);

    }

    private static boolean done() {
        for (int i = 0; i < NUMBER_OF_SERIAL_EXECUTORS; i++) {
            if (orders.get(i) != UPTO) return false;
        }
        return true;
    }
}
