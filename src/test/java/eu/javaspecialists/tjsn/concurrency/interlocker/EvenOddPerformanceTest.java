/*
 * Copyright (C) 2000-2012 Heinz Max Kabutz
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

package eu.javaspecialists.tjsn.concurrency.interlocker;

import eu.javaspecialists.tjsn.concurrency.interlocker.impl.*;
import org.junit.*;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Tests that performance of the lock-free vs blocking interlocker
 * implementations work.
 * <p/>
 * Described in http://www.javaspecialists.eu/archive/Issue188.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class EvenOddPerformanceTest {

    private final Interlocker[] executors = {
            new LockFreeInterlocker(),
            new AtomicInterlocker(),
            new ConditionInterlocker(),
            new WaitNotifyInterlocker(),
            new SemaphoreInterlocker(),
    };

    @Test
    public void testFastInterlocking() throws InterruptedException {
        // warm-up
        raceExecutors();
        raceExecutors();
        // real race
        List<InterlockerResult> results = raceExecutors();

        for (InterlockerResult result : results) {
            System.out.println(result.interlockerClass.getSimpleName() + " in " + result.time + "ms");
        }

        checkLockFreeAreFastest(results);
    }

    private List<InterlockerResult> raceExecutors() throws InterruptedException {
        List<InterlockerResult> results = new ArrayList<>(executors.length);
        for (Interlocker executor : executors) {
            InterlockTask<?> task = new EmptyInterlockTask(100 * 1000);
            long time = System.currentTimeMillis();
            executor.execute(task);
            time = System.currentTimeMillis() - time;
            results.add(new InterlockerResult(executor, time));
        }
        Collections.sort(results);
        return results;
    }

    private void checkLockFreeAreFastest(List<InterlockerResult> results) {
        Iterator<InterlockerResult> it = results.iterator();
        assertTrue(it.next().isLockFree());
        assertTrue(it.next().isLockFree());
        assertFalse(it.next().isLockFree());
        assertFalse(it.next().isLockFree());
        assertFalse(it.next().isLockFree());
        assertFalse(it.hasNext());
    }

    private static class InterlockerResult implements Comparable<InterlockerResult> {
        private final Class<? extends Interlocker> interlockerClass;
        private final long time;

        private InterlockerResult(Interlocker interlocker, long time) {
            this.interlockerClass = interlocker.getClass();
            this.time = time;
        }

        @Override
        public int compareTo(InterlockerResult o) {
            return Long.compare(time, o.time);
        }

        private boolean isLockFree() {
            return interlockerClass == AtomicInterlocker.class ||
                    interlockerClass == LockFreeInterlocker.class;
        }
    }
}