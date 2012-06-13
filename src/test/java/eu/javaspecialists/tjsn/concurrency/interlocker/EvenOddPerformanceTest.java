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
        Map<Long, Class<? extends Interlocker>> results = raceExecutors();

        for (Map.Entry<Long, Class<? extends Interlocker>> entry : results.entrySet()) {
            System.out.println(entry.getValue().getSimpleName() + " in " + entry.getKey() + "ms");
        }

        checkLockFreeAreFastest(results);
    }

    private void checkLockFreeAreFastest(Map<Long, Class<? extends Interlocker>> results) {
        Iterator<Class<? extends Interlocker>> it = results.values().iterator();
        assertTrue(lockFree(it.next()));
        assertTrue(lockFree(it.next()));
        assertFalse(lockFree(it.next()));
        assertFalse(lockFree(it.next()));
        assertFalse(lockFree(it.next()));
        assertFalse(it.hasNext());
    }

    private Map<Long, Class<? extends Interlocker>> raceExecutors() throws InterruptedException {
        Map<Long, Class<? extends Interlocker>> results = new TreeMap<>();
        for (Interlocker executor : executors) {
            InterlockTask<?> task = new EmptyInterlockTask(100 * 1000);
            long time = System.currentTimeMillis();
            executor.execute(task);
            time = System.currentTimeMillis() - time;
            results.put(time, executor.getClass());
        }
        return results;
    }

    private boolean lockFree(Class<? extends Interlocker> interlockerClass) {
        return interlockerClass == AtomicInterlocker.class || interlockerClass == LockFreeInterlocker.class;
    }
}