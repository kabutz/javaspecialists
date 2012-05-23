package eu.javaspecialists.tjsn.examples.issue187;

import java.util.*;

/**
 * Checks whether we have seen duplicate instances of exceptions being thrown.
 *
 * @author Dr Heinz M. Kabutz
 */
public class DuplicateExceptionChecker {
    private final IdentityHashMap<Exception, Boolean> previous =
            new IdentityHashMap<>();

    public void handleException(Exception e) {
        checkForDuplicates(e);
    }

    private void checkForDuplicates(Exception e) {
        Boolean hadPrevious = previous.get(e);
        if (hadPrevious == null) {
            previous.put(e, false);
        } else if (!hadPrevious) {
            notifyOfDuplicate(e);
            previous.put(e, true);
        }
    }

    public void notifyOfDuplicate(Exception e) {
        System.err.println("Duplicate Exception: " + e.getClass());
        System.err.println("count = " + count(e));
        e.printStackTrace();
    }

    private int count(Exception e) {
        int count = 0;
        Class exceptionType = e.getClass();
        for (Exception exception : previous.keySet()) {
            if (exception.getClass() == exceptionType) {
                count++;
            }
        }
        return count;
    }
}