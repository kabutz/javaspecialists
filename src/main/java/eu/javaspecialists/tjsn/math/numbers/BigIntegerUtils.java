package eu.javaspecialists.tjsn.math.numbers;

import java.math.*;

/**
 * Some utility functions for adding an arbitrary number of BigIntegers and for
 * splitting a BigInteger at some position.
 *
 * @author Dr Heinz M. Kabutz
 */
class BigIntegerUtils {
    public static BigInteger add(BigInteger... ints) {
        BigInteger sum = ints[0];
        for (int i = 1; i < ints.length; i++) {
            sum = sum.add(ints[i]);
        }
        return sum;
    }

    public static BigInteger[] split(BigInteger x, int m) {
        BigInteger left = x.shiftRight(m);
        BigInteger right = x.subtract(left.shiftLeft(m));
        return new BigInteger[]{left, right};
    }
}