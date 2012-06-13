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

public class FibonacciTakahashi extends NonCachingFibonacci {
    private static final BigInteger SIX = new BigInteger("6");
    private static final BigInteger FIVE = new BigInteger("5");

    public BigInteger calculate(int n) throws InterruptedException {
        if (Thread.interrupted()) throw new InterruptedException();
        BigInteger F = BigInteger.ONE;
        BigInteger L = BigInteger.ONE;

        int sign = -2;
        int exp = (int) Math.floor((Math.log(n) / Math.log(2)));
        int mask = (int) Math.pow(2, exp);
        for (int i = 0; i < exp - 1; i++) {
            if (Thread.interrupted()) throw new InterruptedException();
            mask = mask >> 1;
            BigInteger F2 = F.pow(2);
            BigInteger FL2 = F.add(L).pow(2);
            F = FL2.subtract(F2.multiply(SIX)).shiftRight(1).subtract(
                    new BigInteger("" + sign));

            if ((n & mask) != 0) {
                BigInteger temp = FL2.shiftRight(2).add(F2);
                L = temp.add(F.shiftLeft(1));
                F = temp;
            } else
                L = F2.multiply(FIVE).add(new BigInteger("" + sign));

            sign = (n & mask) != 0 ? -2 : 2;
        }
        if ((n & (mask >> 1)) == 0)
            return F.multiply(L);
        else
            return F.add(L).shiftRight(1).multiply(L).subtract(BigInteger.valueOf(sign >> 1));
    }
}
