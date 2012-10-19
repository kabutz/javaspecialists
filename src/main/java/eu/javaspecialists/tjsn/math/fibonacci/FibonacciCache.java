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

import eu.javaspecialists.tjsn.concurrency.util.*;

import java.math.*;
import java.util.concurrent.*;

/**
 * A cache specific for Fibonacci numbers.  It contains the optimization that
 * if we request "n" and the cache does not contain that, we see whether "n-1"
 * and "n-2" are contained.  If they are, then we can use those to quickly
 * calculate the Fibonacci value for "n".  Similary, we can look for "n+1" and
 * "n+2" or "n+1" and "n-1" to find "n" quickly.
 * <p/>
 * Cache now reserves a spot if a full calculation is required.  Thus if
 * thread A calls get(1000) and subsequently thread B calls get(1000), then
 * thread B will wait until thread A has put the value in the cache before
 * returning the value.  This prevents duplicate numbers being calculated.
 *
 * @author Dr Heinz M. Kabutz, Joe Bowbeer
 */
final class FibonacciCache {
    private final ConcurrentMap<Integer, FutureResult<BigInteger>> cache =
            new ConcurrentHashMap<>();

    public BigInteger get(int n) throws InterruptedException {
        FutureResult<BigInteger> result = new FutureResult<>();
        FutureResult<BigInteger> pending = cache.putIfAbsent(n, result);
        if (pending != null) {
            return pending.get();
        }
        FutureResult<BigInteger> nMinusOne = cache.get(n - 1);
        FutureResult<BigInteger> nMinusTwo = cache.get(n - 2);
        if (nMinusOne != null && nMinusTwo != null) {
            return put(n, nMinusOne.get().add(nMinusTwo.get()));
        }
        FutureResult<BigInteger> nPlusOne = cache.get(n + 1);
        FutureResult<BigInteger> nPlusTwo = cache.get(n + 2);
        if (nPlusOne != null && nPlusTwo != null) {
            return put(n, nPlusTwo.get().subtract(nPlusOne.get()));
        }
        if (nPlusOne != null && nMinusOne != null) {
            return put(n, nPlusOne.get().subtract(nMinusOne.get()));
        }
        return null;
    }

    public BigInteger put(int n, BigInteger value) {
        cache.get(n).set(value);
        return value;
    }
}