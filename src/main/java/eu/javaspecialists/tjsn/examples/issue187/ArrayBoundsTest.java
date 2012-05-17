package eu.javaspecialists.tjsn.examples.issue187;

import java.util.*;

public class ArrayBoundsTest extends DuplicateExceptionChecker {
    private static final Object[] randomObjects =
            new Object[1000 * 1000];

    private static final int[] randomIndexes =
            new int[1000 * 1000];

    private static final String[] randomStrings =
            new String[1000 * 1000];


    public static void main(String[] args) {
        ArrayBoundsTest test = new ArrayBoundsTest();
        test.fillArrays(0.01);
        test.test();
    }

    public void notifyOfDuplicate(Exception e) {
        super.notifyOfDuplicate(e);
        System.exit(1);
    }

    private void fillArrays(double probabilityIndexIsOut) {
        Random random = new Random(0);
        for (int i = 0; i < randomObjects.length; i++) {
            randomObjects[i] = new Integer(i);
            randomIndexes[i] = (int) (Math.random() * i);
            if (random.nextDouble() < probabilityIndexIsOut) {
                randomIndexes[i] = -randomIndexes[i];
            }
        }
        Arrays.fill(randomStrings, null);
    }

    private void test() {
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < randomObjects.length; j++) {
                try {
                    int index = randomIndexes[j];
                    randomStrings[index] = randomObjects[index].toString();
                } catch (ArrayIndexOutOfBoundsException e) {
                    randomStrings[j] = null;
                    handleException(e);
                }
            }
        }
    }
}