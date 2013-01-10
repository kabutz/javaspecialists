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
import java.util.*;
import java.util.concurrent.*;

/**
 * Fibonacci calculator incorporating a local cache.  This class is not
 * thread-safe.
 *
 * @author Joe Bowbeer
 */
public class FibonacciRecursiveBowbeer extends Fibonacci {

    private final Map<Integer, BigInteger> values = new HashMap<>();

    private final Map<BigInteger, BigInteger> squares = new HashMap<>();

    private final Karatsuba karatsuba;

    public FibonacciRecursiveBowbeer(ForkJoinPool pool) {
        this(new ParallelKaratsuba(pool));
    }

    public FibonacciRecursiveBowbeer() {
        this(new ForkJoinPool());
    }

    public FibonacciRecursiveBowbeer(Karatsuba karatsuba) {
        this.karatsuba = karatsuba;
    }

    @Override
    protected BigInteger doActualCalculate(int n) throws InterruptedException {
        if (Thread.interrupted()) throw new InterruptedException();
        if (n == 0) {
            return BigInteger.ZERO;
        }
        if (n == 1) {
            return BigInteger.ONE;
        }
        int left = (n + 1) / 2;
        int right = left - 1;
        BigInteger fibRight = values.get(right);
        if (fibRight == null) {
            fibRight = doActualCalculate(right);
        }
        BigInteger fibLeft = values.get(left);
        if (fibLeft == null) {
            fibLeft = doActualCalculate(left);
        }
        BigInteger result;
        if ((n & 1) == 0) {
            // f(2n) = (2 * f(n-1) + f(n)) * f(n)
            result = multiply(fibLeft, fibLeft.add(fibRight.shiftLeft(1)));
        } else {
            // f(2n-1) = f(n-1)^2 + f(n)^2
            result = square(fibLeft).add(square(fibRight));
        }
        values.put(n, result);
        return result;
    }

    protected BigInteger multiply(BigInteger x, BigInteger y) {
        return karatsuba.multiply(x, y);
    }

    protected BigInteger square(BigInteger num) {
        BigInteger result = squares.get(num);
        if (result == null) {
            result = karatsuba.square(num);
            squares.put(num, result);
        }
        return result;
    }
}