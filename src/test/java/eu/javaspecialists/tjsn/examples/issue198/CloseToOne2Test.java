package eu.javaspecialists.tjsn.examples.issue198;

import org.junit.*;

import static junit.framework.Assert.assertEquals;

/**
 * Demo class from http://www.javaspecialists.eu/archive/Issue198.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class CloseToOne2Test {
    @Test
    public void testWithLargeIntAddition() {
        double d = 0.999999993;
        int i = (int) (1 + d);
        assertEquals(1, i);
        int j = (int) (100_000_000 + d);
        assertEquals(100_000_001, j);
    }
}
