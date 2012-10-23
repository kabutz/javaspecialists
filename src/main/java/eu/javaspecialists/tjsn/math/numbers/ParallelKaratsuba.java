/*
 * Copyright (C) 2000-2012 Heinz Max Kabutz
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

package eu.javaspecialists.tjsn.math.numbers;

import java.math.*;
import java.util.concurrent.*;

import static eu.javaspecialists.tjsn.math.numbers.BigIntegerUtils.*;

/**
 * The ParallelKaratsuba breaks the number into smaller chunks and then solves
 * it in parallel using fork/join and recursive decomposition.
 * <p/>
 * The threshold for when we use Karatsuba vs when we can use BigInteger
 * multiply() is by default 1000, but can be changed with the system property
 * eu.javaspecialists.tjsn.math.numbers.ParallelKaratsubaThreshold.  For
 * example to set the threshold to 2000, start the JVM with flag
 * -Deu.javaspecialists.tjsn.math.numbers.ParallelKaratsubaThreshold=2000
 *
 * @author Dr Heinz M. Kabutz
 * @author Joe Bowbeer
 */
public class ParallelKaratsuba implements Karatsuba {
    public static final String THRESHOLD_PROPERTY_NAME =
            "eu.javaspecialists.tjsn.math.numbers.ParallelKaratsubaThreshold";
    private static final int THRESHOLD = Integer.getInteger(
            THRESHOLD_PROPERTY_NAME, 1000);
    private final ForkJoinPool pool;

    public ParallelKaratsuba(ForkJoinPool pool) {
        this.pool = pool;
    }

    public BigInteger multiply(BigInteger x, BigInteger y) {
        return pool.invoke(new MultiplyTask(x, y));
    }

    public BigInteger square(BigInteger x) {
        return pool.invoke(new SquareTask(x));
    }

    private static class MultiplyTask extends RecursiveTask<BigInteger> {
        private final BigInteger x, y;

        public MultiplyTask(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }

        protected BigInteger compute() {
            int m = (Math.min(x.bitLength(), y.bitLength()) / 2);
            if (m <= THRESHOLD) {
                return x.multiply(y);
            }

            // x = x1 * 2^m + x0
            // y = y1 * 2^m + y0
            BigInteger[] xs = split(x, m);
            BigInteger[] ys = split(y, m);

            // xy = (x1 * 2^m + x0)(y1 * 2^m + y0) = z2 * 2^2m + z1 * 2^m + z0
            // where:
            // z2 = x1 * y1
            // z0 = x0 * y0
            // z1 = x1 * y0 + x0 * y1 = (x1 + x0)(y1 + y0) - z2 - z0
            MultiplyTask z2task = new MultiplyTask(xs[0], ys[0]);
            MultiplyTask z0task = new MultiplyTask(xs[1], ys[1]);
            MultiplyTask z1task = new MultiplyTask(add(xs), add(ys));

            z0task.fork();
            z2task.fork();
            BigInteger z0, z2;
            BigInteger z1 = z1task.invoke().subtract(
                    z2 = z2task.join()).subtract(z0 = z0task.join());

            // result = z2 * 2^2m + z1 * 2^m + z0
            return z2.shiftLeft(2 * m).add(z1.shiftLeft(m)).add(z0);
        }
    }

    private static class SquareTask extends RecursiveTask<BigInteger> {
        private final BigInteger x;

        public SquareTask(BigInteger x) {
            this.x = x;
        }

        @Override
        protected BigInteger compute() {

            int m = x.bitLength() / 2;
            if (m <= THRESHOLD * 2) {
                return x.pow(2);
            }

            // x = x1 * 2^m + x0
            BigInteger[] xs = split(x, m);

            // x^2 = (x1 * 2^m + x0)(x1 * 2^m + x0) = z2 * 2^2m + z1 * 2^m + z0
            // where:
            // z2 = x1 * x1
            // z0 = x0 * x0
            // z1 = x1 * x0 + x0 * x1 = (x1 + x0)(x1 + x0) - z2 - z0
            SquareTask z2task = new SquareTask(xs[0]);
            SquareTask z0task = new SquareTask(xs[1]);
            SquareTask z1task = new SquareTask(add(xs));

            z0task.fork();
            z2task.fork();
            BigInteger z0, z2;
            BigInteger z1 = z1task.invoke().subtract(
                    z2 = z2task.join()).subtract(z0 = z0task.join());

            // result = z2 * 2^2m + z1 * 2^m + z0
            return z2.shiftLeft(2 * m).add(z1.shiftLeft(m)).add(z0);
        }
    }
}