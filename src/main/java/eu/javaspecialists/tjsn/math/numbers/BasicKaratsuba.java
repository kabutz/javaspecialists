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

import java.math.*;

import static eu.javaspecialists.tjsn.math.numbers.BigIntegerUtils.*;

/**
 * http://en.wikipedia.org/wiki/Karatsuba_algorithm
 * <p/>
 * <p/>
 * To compute the product of 1234 and 5678, choose B = 10 and m = 2. Then
 * <p/>
 * <p/>
 * 12 34 = 12 * 100 + 34<br/>
 * 56 78 = 56 * 100 + 78<br/>
 * z2 = 12 * 56 = 672<br/>
 * z0 = 34 * 78 = 2652<br/>
 * z1 = (12 + 34)(56 + 78) - z2 - z0 = 46 * 134 - 672 - 2652 = 2840<br/>
 * result = z2 * 100*100 + z1 * 100 + z0 = 672 * 10000 + 2840 * 100 + 2652 =
 * 7006652
 * </ol>
 * <p/>
 * The threshold for when we use Karatsuba vs when we can use BigInteger
 * multiply() is by default 1000, but can be changed with the system property
 * eu.javaspecialists.tjsn.math.numbers.BasicKaratsubaThreshold.  For
 * example to set the threshold to 2000, start the JVM with flag
 * -Deu.javaspecialists.tjsn.math.numbers.BasicKaratsubaThreshold=2000
 * <p/>
 * Algorithm also described in Introduction to Programming in Java, ISBN
 * 0321498054.
 * <p/>
 * The square function added by Joe Bowbeer optimizes the special case of
 * squaring two numbers.
 *
 * @author Dr Heinz M. Kabutz
 * @author Joe Bowbeer
 */
public class BasicKaratsuba implements Karatsuba {
  public static final String THRESHOLD_PROPERTY_NAME =
      "eu.javaspecialists.tjsn.math.numbers.BasicKaratsubaThreshold";
  private static final int THRESHOLD = Integer.getInteger(
      THRESHOLD_PROPERTY_NAME, 1000);

  public BigInteger multiply(BigInteger x, BigInteger y) {
    int m = java.lang.Math.min(x.bitLength(), y.bitLength()) / 2;
    if (m <= THRESHOLD)
      return x.multiply(y);

    // x = x1 * 2^m + x0
    // y = y1 * 2^m + y0
    BigInteger[] xs = BigIntegerUtils.split(x, m);
    BigInteger[] ys = BigIntegerUtils.split(y, m);

    // xy = (x1 * 2^m + x0)(y1 * 2^m + y0) = z2 * 2^2m + z1 * 2^m + z0
    // where:
    // z2 = x1 * y1
    // z0 = x0 * y0
    // z1 = x1 * y0 + x0 * y1 = (x1 + x0)(y1 + y0) - z2 - z0
    BigInteger z2 = multiply(xs[0], ys[0]);
    BigInteger z0 = multiply(xs[1], ys[1]);
    BigInteger z1 = multiply(add(xs), add(ys)).
        subtract(z2).subtract(z0);

    // result = z2 * 2^2m + z1 * 2^m + z0
    return z2.shiftLeft(2 * m).add(z1.shiftLeft(m)).add(z0);
  }


  /**
   * Optimizes the square of large numbers using the Karatsuba algorithm.
   *
   * @author Joe Bowbeer
   */
  public BigInteger square(BigInteger x) {

    int m = x.bitLength() / 2;
    if (m <= THRESHOLD * 2) {
      return x.pow(2);
    }

    // x = x1 * 2^m + x0
    BigInteger[] xs = split(x, m);

    // x^2 = (x1 * 2^m + x0)(x1 * 2^m + x0) = z2 * 2^2m + z1 * 2^m + z0
    // where:
    // z2 = x1 * x1
    // z0 = x0 * x0
    // z1 = x1 * x0 + x0 * x1 = (x1 + x0)(x1 + x0) - z2 - z0
    BigInteger z2 = square(xs[0]);
    BigInteger z0 = square(xs[1]);
    BigInteger z1 = square(add(xs)).subtract(z2).subtract(z0);

    // result = z2 * 2^2m + z1 * 2^m + z0
    return z2.shiftLeft(2 * m).add(z1.shiftLeft(m)).add(z0);
  }

}