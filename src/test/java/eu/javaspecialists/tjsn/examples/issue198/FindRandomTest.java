package eu.javaspecialists.tjsn.examples.issue198;

import org.junit.*;

import static org.junit.Assert.assertTrue;

/**
 * Test class for code from http://www.javaspecialists.eu/archive/Issue198.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class FindRandomTest {
    @Test
    public void testThatMinBiggerOrEqualToZero() {
        double minDouble = FindRandom.findMinDouble();
        assertTrue(minDouble >= 0.0);
        assertTrue(minDouble < 1.0);
    }

    @Test
    public void testThatMaxLessThanOne() {
        double maxDouble = FindRandom.findMaxDouble();
        assertTrue(maxDouble < 1.0);
    }
}