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