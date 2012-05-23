package eu.javaspecialists.tjsn.math.fibonacci;

import java.math.*;

/**
 * The non-caching Fibonacci function only has a calculate() method, not
 * doActualCalculate().  It does not use a cache.
 *
 * @author Dr Heinz M. Kabutz
 */
public abstract class NonCachingFibonacci extends Fibonacci {
    protected NonCachingFibonacci() {
        super(null);
    }

    public final BigInteger doActualCalculate(int n)
            throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    public abstract BigInteger calculate(int n) throws InterruptedException;
}
