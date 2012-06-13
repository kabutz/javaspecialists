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

package eu.javaspecialists.tjsn.examples.issue201;

import eu.javaspecialists.tjsn.math.fibonacci.*;

import java.util.concurrent.*;

/**
 * Demo class from http://www.javaspecialists.eu/archive/Issue201.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class FibonacciGeneratorExample {
    private static ForkJoinPool pool = new ForkJoinPool(
            Runtime.getRuntime().availableProcessors() * 4);

    public static void main(String[] args) throws InterruptedException {
        int[] ns;
        if (args.length != 0) {
            ns = new int[args.length];
            for (int i = 0; i < ns.length; i++) {
                ns[i] = Integer.parseInt(args[i]);
            }
        } else {
            ns = new int[]{
                    1_000_000,
                    10_000_000,
                    100_000_000, // takes a bit long
                    1000_000_000, // takes a bit long
            };
        }
        test(new FibonacciRecursiveParallelDijkstraKaratsuba(pool), ns);
    }

    private static void test(Fibonacci fib, int... ns) throws InterruptedException {
        for (int n : ns) {
            FibonacciGenerator fibgen = new FibonacciGenerator(fib);
            fibgen.findFib(n);
            System.out.println(pool);
        }
    }
}