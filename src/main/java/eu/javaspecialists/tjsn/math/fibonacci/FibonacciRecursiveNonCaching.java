package eu.javaspecialists.tjsn.math.fibonacci;

import java.math.*;

/**
 * This algorithm calculates the values exponentially by redoing a lot of work.
 * The complexity is actually another fibonacci series, which ends up
 * increasing exponentially.  We can only calculate for very small values of n.
 *
 * @author Dr Heinz M. Kabutz
 */
public class FibonacciRecursiveNonCaching extends NonCachingFibonacci {
    public BigInteger calculate(int n) throws InterruptedException {
        if (Thread.interrupted()) throw new InterruptedException();
        if (n < 0) throw new IllegalArgumentException();
        if (n == 0) return BigInteger.ZERO;
        if (n == 1) return BigInteger.ONE;
        return calculate(n - 1).add(calculate(n - 2));
    }
}