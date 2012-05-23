package eu.javaspecialists.tjsn.math.fibonacci;

import java.math.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * A cache specific for Fibonacci numbers.  It contains the optimization that
 * if
 * we request "n" and the cache does not contain that, we see whether "n-1" and
 * "n-2" are contained.  If they are, then we can use those to quickly
 * calculate
 * the Fibonacci value for "n".  Similary, we can look for "n+1" and "n+2" or
 * "n+1" and "n-1" to find "n" quickly.
 *
 * @author Dr Heinz M. Kabutz
 */
class FibonacciCache {
    private final Map<Integer, BigInteger> cache =
            new ConcurrentHashMap<>();

    public BigInteger get(int n) {
        BigInteger result = cache.get(n);
        if (result != null) {
            return result;
        }
        BigInteger nMinusOne = cache.get(n - 1);
        BigInteger nMinusTwo = cache.get(n - 2);
        if (nMinusOne != null && nMinusTwo != null) {
            result = nMinusOne.add(nMinusTwo);
            cache.put(n, result);
            return result;
        }
        BigInteger nPlusOne = cache.get(n + 1);
        BigInteger nPlusTwo = cache.get(n + 2);
        if (nPlusOne != null && nPlusTwo != null) {
            result = nPlusTwo.subtract(nPlusOne);
            cache.put(n, result);
            return result;
        }
        if (nPlusOne != null && nMinusOne != null) {
            result = nPlusOne.subtract(nMinusOne);
            cache.put(n, result);
            return result;
        }
        return null;
    }

    public void put(int n, BigInteger value) {
        cache.put(n, value);
    }

    /**
     * Resets the cache of intermittent values.
     */
    public void reset() {
        cache.clear();
    }
}
