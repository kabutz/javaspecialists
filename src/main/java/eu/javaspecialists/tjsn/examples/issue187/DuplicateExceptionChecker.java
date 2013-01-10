/*
 * Copyright (C) 2000-2013 Heinz Max Kabutz
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Heinz Max Kabutz licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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