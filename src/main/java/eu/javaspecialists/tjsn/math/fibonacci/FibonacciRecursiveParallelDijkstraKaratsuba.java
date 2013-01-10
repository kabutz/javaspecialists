/*
 * Copyright (C) 2000-2013 Heinz Max Kabutz
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Heinz Max Kabutz licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
 * <p/>
 * The threshold for when we use a sequential vs parallel solution for
 * Fibonacci is by default 10000, but can be changed with the system property
 * eu.javaspecialists.tjsn.math.fibonacci.SequentialThreshold.  For
 * example to set the threshold to 20000, start the JVM with flag
 * -Deu.javaspecialists.tjsn.math.fibonacci.SequentialThreshold=20000
 * <p/>
 * This Fibonacci solution does its own internal caching using the
 * FibonacciCache.
 *
 * @author Dr Heinz M. Kabutz
 */
public class FibonacciRecursiveParallelDijkstraKaratsuba
        extends NonCachingFibonacci {
    public static final String SEQUENTIAL_THRESHOLD_PROPERTY_NAME =
            "eu.javaspecialists.tjsn.math.fibonacci.SequentialThreshold";
    private final static int SEQUENTIAL_THRESHOLD = Integer.getInteger(
            SEQUENTIAL_THRESHOLD_PROPERTY_NAME, 10000);
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
            try {
                BigInteger result = cache.get(n);
                if (result != null) {
                    return result;
                }
                //System.out.println("Computing fib(" + n + ")");

                if (n < SEQUENTIAL_THRESHOLD) {
                    result = sequential.calculate(n);
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
                //System.out.println("Adding result of fib(" + n + ") to cache");
                cache.put(n, result);
                return result;
            } catch (InterruptedException e) {
                cancel(true);
                return null;
            }
        }

        private BigInteger square(BigInteger num) {
            return karatsuba.multiply(num, num);
        }
    }
}