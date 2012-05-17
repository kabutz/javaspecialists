package eu.javaspecialists.tjsn.examples.issue198;

import java.util.*;

/**
 * Demo class from http://www.javaspecialists.eu/archive/Issue198.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class MathTeaser {
    public static void main(String[] args) {
        final Random random = new Random();
        Thread seeder = new Thread() {
            public void run() {
                while (true) {
                    random.setSeed(51102269); // causes 2^26-1 as next(26)
                    random.setSeed(223209395); // causes 2^27-1 as next(27)
                }
            }
        };
        seeder.setDaemon(true);
        seeder.start();

        while (true) {
            double num = random.nextDouble();
            if ((int) (num + 1) == 2) {
                System.out.println("Yes, random.nextDouble() can: " + num);
                break;
            }
        }
    }
}
