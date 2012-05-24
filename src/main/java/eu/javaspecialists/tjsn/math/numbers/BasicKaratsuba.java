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
 *
 * @author Dr Heinz M. Kabutz
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
}