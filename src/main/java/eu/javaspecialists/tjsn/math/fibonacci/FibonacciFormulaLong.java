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
 * Let phi = (1+root5)/2 and psi = (1-root5)/2.  Fibonacci(n) can be calculated
 * by (phi^n - psi^n) / (phi - psi) or shorter (phi^n - psi^n) / root5.
 * <p/>
 * <p/>
 * Calculates up to Fibonacci(71).
 *
 * @author Dr Heinz M. Kabutz
 */
public class FibonacciFormulaLong extends NonCachingFibonacci {
    private static final double root5 = Math.sqrt(5);
    private static final double PHI = (1 + root5) / 2;
    private static final double PSI = (1 - root5) / 2;
    private static final int MAXIMUM_PRECISE_NUMBER = 71;

    public BigInteger calculate(int n) throws InterruptedException {
        if (Thread.interrupted()) throw new InterruptedException();
        if (n < 0) throw new IllegalArgumentException();
        if (n > MAXIMUM_PRECISE_NUMBER) throw new IllegalArgumentException(
                "Precision loss after " + MAXIMUM_PRECISE_NUMBER);
        return new BigInteger(Long.toString(fibWithFormula(n)));
    }

    private static long fibWithFormula(int n) {
        return (long) ((Math.pow(PHI, n) - Math.pow(PSI, n)) / root5);
    }
}