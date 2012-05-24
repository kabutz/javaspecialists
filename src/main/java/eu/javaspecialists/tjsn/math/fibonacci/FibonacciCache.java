package eu.javaspecialists.tjsn.math.fibonacci;

import java.math.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

/**
 * A cache specific for Fibonacci numbers.  It contains the optimization that
 * if we request "n" and the cache does not contain that, we see whether "n-1"
 * and "n-2" are contained.  If they are, then we can use those to quickly
 * calculate the Fibonacci value for "n".  Similary, we can look for "n+1" and
 * "n+2" or "n+1" and "n-1" to find "n" quickly.
 * <p/>
 * Cache now reserves a spot if a full calculation is required.  Thus if
 * thread A calls get(1000) and subsequently thread B calls get(1000), then
 * thread B will wait until thread A has put the value in the cache before
 * returning the value.  This prevents duplicate numbers being calculated.
 *
 * @author Dr Heinz M. Kabutz
 */
class FibonacciCache {
    private final ConcurrentMap<Integer, BigInteger> cache =
            new ConcurrentHashMap<>();

    private final static boolean withReservation = false;
    private final Lock lock = new ReentrantLock();
    private final Condition solutionArrived = lock.newCondition();
    private final Set<Integer> cacheReservation = new HashSet<>();

    public BigInteger get(int n) throws InterruptedException {
        lock.lock();
        try {
            if (withReservation) {
                while (cacheReservation.contains(n)) {
                    // we now want to wait until the answer is in the cache
                    solutionArrived.await();
                }
            }
            BigInteger result = cache.get(n);
            if (result != null) {
                return result;
            }

            BigInteger nMinusOne = cache.get(n - 1);
            BigInteger nMinusTwo = cache.get(n - 2);
            if (nMinusOne != null && nMinusTwo != null) {
                result = nMinusOne.add(nMinusTwo);
                put(n, result);
                return result;
            }
            BigInteger nPlusOne = cache.get(n + 1);
            BigInteger nPlusTwo = cache.get(n + 2);
            if (nPlusOne != null && nPlusTwo != null) {
                result = nPlusTwo.subtract(nPlusOne);
                put(n, result);
                return result;
            }
            if (nPlusOne != null && nMinusOne != null) {
                result = nPlusOne.subtract(nMinusOne);
                put(n, result);
                return result;
            }
            cacheReservation.add(n);
            return null;
        } finally {
            lock.unlock();
        }
    }

    public void put(int n, BigInteger value) {
        lock.lock();
        try {
            solutionArrived.signalAll();
            cacheReservation.remove(n);
            cache.putIfAbsent(n, value);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Resets the cache of intermittent values.  This should not clear the
     * cacheReservation map as we might have threads waiting for values that
     * are being calculated.
     */
    public void reset() {
        cache.clear();
    }
}