package eu.javaspecialists.tjsn.math.numbers;

import org.junit.*;

import java.math.*;
import java.util.concurrent.*;

import static org.junit.Assert.*;

/**
 * Various tests that check that the Karatsuba multiply works as expected.  It
 * also compares the performance of Karatsuba versus standard multiply.
 *
 * @author Dr Heinz M. Kabutz
 */
public class KaratsubaTest {
    @Test
    public void testKaratsubaEqualsBigIntegerMultiply() throws InterruptedException {
        checkKaratsubaEqualsBigIntegerMultiply(new BasicKaratsuba());
        ForkJoinPool pool = new ForkJoinPool();
        checkKaratsubaEqualsBigIntegerMultiply(new ParallelKaratsuba(pool));
        pool.shutdown();
        assertTrue(pool.awaitTermination(1, TimeUnit.SECONDS));
        System.out.println("pool = " + pool);
    }

    private void checkKaratsubaEqualsBigIntegerMultiply(Karatsuba karatsuba) {
        for (int N = 1; N <= 100_000; N *= 10) {
            BigInteger a = new BigInteger(N, ThreadLocalRandom.current());
            BigInteger b = new BigInteger(N, ThreadLocalRandom.current());
            BigInteger c = karatsuba.multiply(a, b);
            BigInteger d = a.multiply(b);
            assertEquals(c, d);
        }
    }

    @Test
    public void testNegativeKaratsubaEqualsBigIntegerMultiply() throws InterruptedException {
        checkNegativeKaratsubaEqualsBigIntegerMultiply(new BasicKaratsuba());
        ForkJoinPool pool = new ForkJoinPool();
        checkNegativeKaratsubaEqualsBigIntegerMultiply(new ParallelKaratsuba(pool));
        pool.shutdown();
        assertTrue(pool.awaitTermination(1, TimeUnit.SECONDS));
        System.out.println("pool = " + pool);
    }

    public void checkNegativeKaratsubaEqualsBigIntegerMultiply(Karatsuba karatsuba) {
        BigInteger a, b;

        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        a = new BigInteger(10_000, rnd);
        b = new BigInteger(10_000, rnd).negate();
        assertEquals(a.multiply(b), karatsuba.multiply(a, b));

        a = new BigInteger(10_000, rnd).negate();
        b = new BigInteger(10_000, rnd);
        assertEquals(a.multiply(b), karatsuba.multiply(a, b));

        a = new BigInteger(10_000, rnd).negate();
        b = new BigInteger(10_000, rnd).negate();
        assertEquals(a.multiply(b), karatsuba.multiply(a, b));
    }

    @Test
    public void testKaratsubaPerformance() throws InterruptedException {
        checkKaratsubaPerformance(new BasicKaratsuba());
        ForkJoinPool pool = new ForkJoinPool();
        checkKaratsubaPerformance(new ParallelKaratsuba(pool));
        pool.shutdown();
        assertTrue(pool.awaitTermination(1, TimeUnit.SECONDS));
        System.out.println("pool = " + pool);
    }

    public void checkKaratsubaPerformance(Karatsuba karatsuba) {
        int N = 1_000_000;
        BigInteger a = new BigInteger(N, ThreadLocalRandom.current());
        BigInteger b = new BigInteger(N, ThreadLocalRandom.current());

        for (int warmup = 0; warmup < 3; warmup++) {
            karatsuba.multiply(a, b);
            a.multiply(b);
        }

        long karatsuba_time = System.currentTimeMillis();
        karatsuba.multiply(a, b);
        karatsuba_time = System.currentTimeMillis() - karatsuba_time;

        long multiply_time = System.currentTimeMillis();
        a.multiply(b);
        multiply_time = System.currentTimeMillis() - multiply_time;

        System.out.println("karatsuba_time = " + karatsuba_time);
        System.out.println("multiply_time = " + multiply_time);
        assertTrue(multiply_time > karatsuba_time);
    }
}