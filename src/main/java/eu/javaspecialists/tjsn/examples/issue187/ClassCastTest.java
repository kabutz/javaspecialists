package eu.javaspecialists.tjsn.examples.issue187;

import java.util.*;

public class ClassCastTest extends DuplicateExceptionChecker {
    private final Object[] randomObjects =
            new Object[1000 * 1000];

    private final String[] randomStrings =
            new String[1000 * 1000];

    public static void main(String[] args) {
        ClassCastTest npt = new ClassCastTest();
        npt.fillArrays(0.01);
        npt.test();
    }

    public void notifyOfDuplicate(Exception e) {
        super.notifyOfDuplicate(e);
        System.exit(1);
    }

    private void fillArrays(double probabilityObjectIsNull) {
        Random random = new Random(0);
        for (int i = 0; i < randomObjects.length; i++) {
            if (random.nextDouble() < probabilityObjectIsNull) {
                randomObjects[i] = new Float(i);
            } else {
                randomObjects[i] = new Integer(i);
            }
        }
        Arrays.fill(randomStrings, null);
    }

    private void test() {
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < randomObjects.length; j++) {
                try {
                    randomStrings[j] = ((Integer) randomObjects[j]).toString();
                } catch (ClassCastException e) {
                    randomStrings[j] = null;
                    handleException(e);
                }
            }
        }
    }
}