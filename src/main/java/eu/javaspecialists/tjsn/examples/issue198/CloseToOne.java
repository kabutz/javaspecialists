package eu.javaspecialists.tjsn.examples.issue198;

/**
 * Demo class from http://www.javaspecialists.eu/archive/Issue198.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class CloseToOne {
    public static double makeDouble(long first, long second) {
        return ((first << 27) + second) / (double) (1L << 53);
    }

    public static void main(String[] args) {

        long first = (1 << 26) - 1;
        long second = (1 << 27) - 1;

        System.out.println(makeDouble(first, second));
        System.out.println((int) (makeDouble(first, second) + 1));

        second--;
        System.out.println(makeDouble(first, second));
        System.out.println((int) (makeDouble(first, second) + 1));

        System.out.println((((1L << 53) - 1)) / (double) (1L << 53));
    }
}