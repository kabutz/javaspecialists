package eu.javaspecialists.tjsn.math.fibonacci;

import eu.javaspecialists.tjsn.math.numbers.*;

import java.math.*;
import java.util.concurrent.*;

/**
 * Based on Dijkstra's sum of the squares, available here:
 * http://www.maths.surrey.ac.uk/hosted-sites/R.Knott/Fibonacci/fibFormula.html
 * <p/>
 * This algorithm uses the Fork/Join framework to parallelize the work over all
 * the cores in the machine and is thus able to achieve good speedups.  Both
 * the sum-of-the-squares Fibonacci algorithm and Karatsuba take advantage of
 * parallelism.
 *
 * @author Dr Heinz M. Kabutz
 */
public class FibonacciRecursiveParallelDijkstraKaratsuba extends NonCachingFibonacci {
    private final static int SEQUENTIAL_THRESHOLD = 10000;
    private final FibonacciCache cache = new FibonacciCache();
    private final Fibonacci sequential =
            new FibonacciRecursiveDijkstraKaratsuba();
    private final ForkJoinPool pool;
    private final Karatsuba karatsuba;

    public FibonacciRecursiveParallelDijkstraKaratsuba(ForkJoinPool pool) {
        this.pool = pool;
        karatsuba = new ParallelKaratsuba(pool);
    }

    public BigInteger calculate(int n) throws InterruptedException {
        if (Thread.interrupted()) throw new InterruptedException();
        BigInteger result = pool.invoke(new FibonacciTask(n));
        if (result == null) throw new InterruptedException();
        return result;
    }

    private class FibonacciTask extends RecursiveTask<BigInteger> {
        private final int n;

        private FibonacciTask(int n) {
            this.n = n;
        }

        protected BigInteger compute() {
            BigInteger result = cache.get(n);
            if (result != null) {
                return result;
            }

            if (n < SEQUENTIAL_THRESHOLD) {
                try {
                    result = sequential.calculate(n);
                } catch (InterruptedException e) {
                    cancel(true);
                    return null;
                }
            } else {
                if (n % 2 == 1) {
                    // f(2n-1) = f(n-1)^2 + f(n)^2
                    int left = (n + 1) / 2;
                    int right = (n + 1) / 2 - 1;
                    FibonacciTask f0 = new FibonacciTask(left);
                    FibonacciTask f1 = new FibonacciTask(right);
                    f1.fork();
                    BigInteger bi0 = f0.invoke();
                    BigInteger bi1 = f1.join();
                    if (isCancelled()) return null;
                    result = square(bi1).add(square(bi0));
                } else {
                    // f(2n) = (2 * f(n-1) + f(n)) * f(n)
                    int n_ = n / 2;
                    FibonacciTask f0 = new FibonacciTask(n_);
                    FibonacciTask f1 = new FibonacciTask(n_ - 1);
                    f1.fork();
                    BigInteger bi0 = f0.invoke();
                    BigInteger bi1 = f1.join();
                    if (isCancelled()) return null;
                    result = karatsuba.multiply(bi1.add(bi1).add(bi0), bi0);
                }
            }
            cache.put(n, result);
            return result;
        }

        private BigInteger square(BigInteger num) {
            return karatsuba.multiply(num, num);
        }
    }
}