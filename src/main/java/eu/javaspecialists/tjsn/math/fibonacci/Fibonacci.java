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