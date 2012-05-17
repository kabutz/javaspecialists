package eu.javaspecialists.tjsn.examples.issue198;

import org.junit.*;

import static junit.framework.Assert.assertEquals;

/**
 * Demo class from http://www.javaspecialists.eu/archive/Issue198.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class CloseToOneTest {
    @Test
    public void testClosestToOne() {
        long first = (1 << 26) - 1;
        long second = (1 << 27) - 1;

        assertEquals(2, (int) (CloseToOne.makeDouble(first, second) + 1));

        second--;

        assertEquals(1, (int) (CloseToOne.makeDouble(first, second) + 1));
    }
}