package eu.javaspecialists.tjsn.math.fibonacci;

import eu.javaspecialists.tjsn.math.numbers.*;

import java.math.*;

/**
 * Based on Dijkstra's sum of the squares, available here:
 * http://www.maths.surrey.ac.uk/hosted-sites/R.Knott/Fibonacci/fibFormula.html
 * <p/>
 * However, instead of the slow BigInteger multiply(), we use Karatsuba's
 * algorithm.  Even faster is JScience's class, which uses a combination of
 * algorithms, depending on the size of the number being multiplied.
 *
 * @author Dr Heinz M. Kabutz
 */
public class FibonacciRecursiveDijkstraKaratsuba
        extends FibonacciRecursiveDijkstra {
    private final Karatsuba karatsuba = new BasicKaratsuba();

    protected BigInteger multiply(BigInteger bi0, BigInteger bi1) {
        return karatsuba.multiply(bi0, bi1);
    }
}