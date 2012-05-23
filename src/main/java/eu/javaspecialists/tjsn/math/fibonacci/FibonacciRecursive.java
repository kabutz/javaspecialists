package eu.javaspecialists.tjsn.math.fibonacci;

import java.math.*;

/**
 * Same as the FibonacciRecursiveNonCaching, except that this algorithm does
 * cache previous values.  It suffers from the same problems as
 * FibonacciIterative, in that the BigInteger.add() method is linear to the
 * size
 * of the number.  In addition, because it is still recursive in nature, the
 * stack depth is a limiting factor in which numbers we can calculate.
 *
 * @author Dr Heinz M. Kabutz
 */
public class FibonacciRecursive extends Fibonacci {
    public BigInteger doActualCalculate(int n) throws InterruptedException {
        if (Thread.interrupted()) throw new InterruptedException();
        if (n < 0) throw new IllegalArgumentException();
        if (n == 0) return BigInteger.ZERO;
        if (n == 1) return BigInteger.ONE;
        return calculate(n - 1).add(calculate(n - 2));
    }
}