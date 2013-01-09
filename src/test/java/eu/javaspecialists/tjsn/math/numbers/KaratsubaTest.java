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

  @Test
  public void testKaratsubaSquare() throws InterruptedException {
    for (int i = 0; i < 10; i++) {
      test();
    }
  }

  private void test() throws InterruptedException {
    ForkJoinPool pool = new ForkJoinPool();
    BigInteger a = new BigInteger(1_000_000, ThreadLocalRandom.current());
    BigInteger squareOfA = a.pow(2);
    checkKaratsubaSquare(a, squareOfA, new BasicKaratsuba());
    checkKaratsubaSquare(a, squareOfA, new ParallelKaratsuba(pool));
    pool.shutdown();
    assertTrue(pool.awaitTermination(1, TimeUnit.SECONDS));
    System.out.println("pool = " + pool);
  }

  private void checkKaratsubaSquare(BigInteger a, BigInteger squareOfA, Karatsuba karatsuba) {
    long time = System.currentTimeMillis();
    assertEquals(squareOfA, karatsuba.square(a));
    time = System.currentTimeMillis() - time;
    System.out.println("Time for " + karatsuba.getClass().getSimpleName() + " square: " + time);
    time = System.currentTimeMillis();
    assertEquals(squareOfA, karatsuba.multiply(a, a));
    time = System.currentTimeMillis() - time;
    System.out.println("Time for " + karatsuba.getClass().getSimpleName() + " multiply: " + time);
  }
}