package eu.javaspecialists.tjsn.examples.issue187;

import java.util.*;

/**
 * Demonstrates different exceptions being eliminated by the JVM.
 *
 * @author Dr Heinz M. Kabutz
 */
public class MixedExeptionTest extends DuplicateExceptionChecker {
    private static final Object[] randomObjects =
            new Object[1000 * 1000];

    private static final int[] randomIndexes =
            new int[1000 * 1000];

    private static final String[] randomStrings =
            new String[1000 * 1000];


    public static void main(String[] args) {
        MixedExeptionTest test = new MixedExeptionTest();
        test.fillArrays(0.01);
        test.test();
    }

    private int duplicates = 0;

    public void notifyOfDuplicate(Exception e) {
        super.notifyOfDuplicate(e);
        duplicates++;
        if (duplicates == 3) {
            System.exit(1);
        }
    }

    private void fillArrays(double probabilityIndexIsOut) {
        Random random = new Random(0);
        for (int i = 0; i < randomObjects.length; i++) {
            randomObjects[i] = new Integer(i);
            randomIndexes[i] = (int) (Math.random() * i);
            if (random.nextDouble() < probabilityIndexIsOut) {
                switch (i % 3) {
                    case 0:
                        randomIndexes[i] = -randomIndexes[i];
                        break;
                    case 1:
                        randomObjects[i] = null;
                        break;
                    case 2:
                        randomObjects[i] = new Float(i);
                        break;
                }
            }
        }
        Arrays.fill(randomStrings, null);
    }

    private void test() {
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < randomObjects.length; j++) {
                try {
                    int index = randomIndexes[j];
                    randomStrings[index] =
                            ((Integer) randomObjects[index]).toString();
                } catch (Exception e) {
                    randomStrings[j] = null;
                    handleException(e);
                    e.printStackTrace();
                }
            }
        }
    }
}