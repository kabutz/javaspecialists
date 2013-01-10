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

package eu.javaspecialists.tjsn.examples.issue201;

import eu.javaspecialists.tjsn.math.fibonacci.*;

import java.math.*;
import java.util.zip.*;

/**
 * Demo class from http://www.javaspecialists.eu/archive/Issue201.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class FibonacciGenerator {
    private final Fibonacci fib;

    public FibonacciGenerator(Fibonacci fib) {
        this.fib = fib;
    }

    public void findFib(int n) throws InterruptedException {
        System.out.printf("Searching for Fibonacci(%,d)%n", n);
        long time = System.currentTimeMillis();
        BigInteger num = fib.calculate(n);
        time = System.currentTimeMillis() - time;
        printProof(num);
        System.out.printf("  Time to calculate %d ms%n%n", time);
    }

    private void printProof(BigInteger num) {
        System.out.printf("  Number of bits: %d%n", num.bitLength());
        byte[] numHex = num.toByteArray();
        System.out.print("  First 10 bytes: ");
        for (int i = 0; i < 10; i++) {
            System.out.printf(" %02x", numHex[i]);
        }
        System.out.println();

        System.out.print("  Last 10 bytes:  ");
        for (int i = numHex.length - 10; i < numHex.length; i++) {
            System.out.printf(" %02x", numHex[i]);
        }
        System.out.println();

        Checksum ck = new Adler32();
        ck.update(numHex, 0, numHex.length);
        System.out.printf("  Adler32 Checksum: 0x%016x%n", ck.getValue());
    }

}
