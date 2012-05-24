package eu.javaspecialists.tjsn.math.fibonacci;

import java.math.*;

/**
 * Let phi = (1+root5)/2 and psi = (1-root5)/2.  Fibonacci(n) can be calculated
 * by (phi^n - psi^n) / (phi - psi) or shorter (phi^n - psi^n) / root5.
 * <p/>
 * Calculates up to Fibonacci(1000).
 *
 * @author Dr Heinz M. Kabutz
 */
public class FibonacciFormulaBigInteger extends NonCachingFibonacci {
    private static final BigDecimal root5 = new BigDecimal("" +
            "2.23606797749978969640917366873127623544061835961152572427089724" +
            "5410520925637804899414414408378782274969508176150773783504253267" +
            "7244470738635863601215334527088667781731918791658112766453226398" +
            "5658053576135041753378");
    private static final BigDecimal PHI = root5.add(new BigDecimal(1)).
            divide(new BigDecimal(2));
    private static final BigDecimal PSI = root5.subtract(new BigDecimal(1)).
            divide(new BigDecimal(2));
    private static final int MAXIMUM_PRECISE_NUMBER = 1000;

    public BigInteger calculate(int n) throws InterruptedException {
        if (Thread.interrupted()) throw new InterruptedException();
        if (n < 0) throw new IllegalArgumentException();
        if (n > MAXIMUM_PRECISE_NUMBER) throw new IllegalArgumentException(
                "Precision loss after " + MAXIMUM_PRECISE_NUMBER);

        BigDecimal phiToTheN = PHI.pow(n);
        if (Thread.interrupted()) throw new InterruptedException();
        BigDecimal psiToTheN = PSI.pow(n);
        if (Thread.interrupted()) throw new InterruptedException();
        BigDecimal phiMinusPsi = phiToTheN.subtract(psiToTheN);
        BigDecimal result = phiMinusPsi.divide(root5, 0, RoundingMode.UP);
        return result.toBigInteger();
    }
}