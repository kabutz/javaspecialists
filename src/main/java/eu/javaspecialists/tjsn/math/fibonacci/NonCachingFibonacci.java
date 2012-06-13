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
 * The non-caching Fibonacci function only has a calculate() method, not
 * doActualCalculate().  It does not use a cache.
 *
 * @author Dr Heinz M. Kabutz
 */
public abstract class NonCachingFibonacci extends Fibonacci {
    protected NonCachingFibonacci() {
        super(null);
    }

    public final BigInteger doActualCalculate(int n)
            throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    public abstract BigInteger calculate(int n)
            throws InterruptedException;
}
