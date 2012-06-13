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
 * Calculates up to Fibonacci(1000).
 *
 * @author Dr Heinz M. Kabutz
 */
public class FibonacciFormulaBigInteger extends NonCachingFibonacci {
    private static final BigDecimal root5 = new BigDecimal("" +
            "2.23606797749978969640917366873127623544061835961152572427089724" +
            "5410520925637804899414414408378782274969508176150773783504253267" +
            "7244470738635863601215334527088667781731918791658112766453226398" +
            "5658053576135041753378");
    private static final BigDecimal PHI = root5.add(new BigDecimal(1)).
            divide(new BigDecimal(2));
    private static final BigDecimal PSI = root5.subtract(new BigDecimal(1)).
            divide(new BigDecimal(2));
    private static final int MAXIMUM_PRECISE_NUMBER = 1000;

    public BigInteger calculate(int n) throws InterruptedException {
        if (Thread.interrupted()) throw new InterruptedException();
        if (n < 0) throw new IllegalArgumentException();
        if (n > MAXIMUM_PRECISE_NUMBER) throw new IllegalArgumentException(
                "Precision loss after " + MAXIMUM_PRECISE_NUMBER);

        BigDecimal phiToTheN = PHI.pow(n);
        if (Thread.interrupted()) throw new InterruptedException();
        BigDecimal psiToTheN = PSI.pow(n);
        if (Thread.interrupted()) throw new InterruptedException();
        BigDecimal phiMinusPsi = phiToTheN.subtract(psiToTheN);
        BigDecimal result = phiMinusPsi.divide(root5, 0, RoundingMode.UP);
        return result.toBigInteger();
    }
}