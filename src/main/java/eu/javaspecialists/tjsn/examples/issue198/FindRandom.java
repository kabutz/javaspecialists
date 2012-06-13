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

package eu.javaspecialists.tjsn.examples.issue198;

/**
 * Demo class from http://www.javaspecialists.eu/archive/Issue198.html
 *
 * @author Dr Wolfgang Laun, Dr Heinz M. Kabutz
 */
public class FindRandom {
    private final static long multiplier = 0x5DEECE66DL;
    private final static long addend = 0xBL;
    private final static long mask = (1L << 48) - 1;

    public static double makeDouble(long pre, long post) {
        return ((pre << 27) + post) / (double) (1L << 53);
    }

    private static long setbits(int len, int off) {
        long res = (1L << len) - 1;
        return res << off;
    }

    /**
     * A random double is composed from two successive random integers. The
     * most
     * significant 26 bits of the first one are taken and concatenated with the
     * most significant 27 bits of the second one. (ms(ri1,26)) << 27 +
     * (ms(ri2,27)) This is divided by (double)(1L << 53) to obtain a result in
     * [0.0, 1.0). To find the maximum random double, we assume that
     * (ms(ri1,m26)) is a maximum (all 1b) and vary the remaining 22 bits from
     * 0
     * to m22, inclusive. Assuming this to be ri1, we perform the calculation
     * according to Random.next() to obtain is successor, our ri2. The maximum
     * of the most significant 27 bits of all ri2 would then be the second part
     * of the maximum 53-bit integer used for a double random's mantissa.
     */
    public static double findMaxDouble() {
        long ones = setbits(26, 22);
        System.out.println("ones: " + Long.toHexString(ones));
        long maxpost = setbits(22, 0);
        System.out.println("maxpost: " + Long.toHexString(maxpost));
        long maxintw = 0;
        for (long post = 0; post <= maxpost; post++) {
            long oldseed = ones + post;
            long nextseed = (oldseed * multiplier + addend) & mask;
            long intw = nextseed >>> (48 - 27);
            if (intw > maxintw) {
                maxintw = intw;
            }
        }
        System.out.println("maxintw: " + Long.toHexString(maxintw));
        long b26 = setbits(26, 0);
        System.out.println("b26: " + Long.toHexString(b26));
        double d = makeDouble(b26, maxintw);
        System.out.println("max. double: " +
                Double.toHexString(d) + " = " + d);
        return d;
    }

    public static double findMinDouble() {
        long b26 = 0L;
        long zeroes = 0L;
        long maxpost = setbits(22, 0);
        long minintw = 0x7fffffffffffffffL;
        for (int post = 0; post < maxpost; post++) {
            long oldseed = zeroes + post;
            long nextseed = (oldseed * multiplier + addend) & mask;
            long intw = nextseed >>> (48 - 27);
            if (intw < minintw) {
                minintw = intw;
            }
        }
        System.out.println("minintw: " + minintw);
        double d = makeDouble(b26, minintw);
        System.out.println("min. double: " +
                Double.toHexString(d) + " = " + d);
        return d;
    }

    public static void main(String[] args) {
        findMaxDouble();
        double belowOne = Math.nextAfter(1.0, 0.0);
        System.out.println("Biggest double below 1.0 is: " +
                Double.toHexString(belowOne) + " = " + belowOne);
        findMinDouble();
    }
}
