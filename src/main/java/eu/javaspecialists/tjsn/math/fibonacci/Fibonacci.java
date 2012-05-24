package eu.javaspecialists.tjsn.math.fibonacci;

import java.math.*;

/**
 * http://www.maths.surrey.ac.uk/hosted-sites/R.Knott/Fibonacci/fibFormula.html
 *
 * @author Dr Heinz M. Kabutz
 */
public abstract class Fibonacci {
    private final FibonacciCache cache;

    protected Fibonacci(FibonacciCache cache) {
        this.cache = cache;
    }

    public Fibonacci() {
        this(null);
    }

    public BigInteger calculate(int n) throws InterruptedException {
        if (cache == null) return doActualCalculate(n);

        BigInteger result = cache.get(n);
        if (result == null) {
            cache.put(n, result = doActualCalculate(n));
        }
        return result;
    }

    protected abstract BigInteger doActualCalculate(int n)
            throws InterruptedException;
}