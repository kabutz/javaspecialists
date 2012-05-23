package eu.javaspecialists.tjsn.math.numbers;

import java.math.*;

/**
 * Karatsuba's big number multiplication algorithm, might be a lot faster than
 * ordinary BigInteger multiply.  We give two implementations, the
 * BasicKaratsuba for single-threaded calculations and the ParallelKaratsuba
 * that utilizes the fork/join framework.
 *
 * @author Dr Heinz M. Kabutz
 */
public interface Karatsuba {
    BigInteger multiply(BigInteger x, BigInteger y);
}
