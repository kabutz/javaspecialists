package eu.javaspecialists.tjsn.examples.issue201;

import eu.javaspecialists.tjsn.math.fibonacci.*;

import java.math.*;
import java.util.zip.*;

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
