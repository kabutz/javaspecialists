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

package eu.javaspecialists.tjsn.examples.issue185;

import java.util.*;

public class ResurrectingTheDead {
    private static final Map<Zombie, Zombie> lruCache =
            new LinkedHashMap<Zombie, Zombie>(1000, 0.75f, true) {
                protected boolean removeEldestEntry(
                        Map.Entry<Zombie, Zombie> eldest) {
                    return size() > 1000;
                }
            };

    public static void main(String[] args) throws Exception {
        while (true) {
            // We create a bunch of zombies, which will take at least
            // 24 bytes each on a 64 bit machine
            for (int i = 0; i < 100 * 1000; i++) {
                new Zombie();
            }

            System.out.printf("UsedMem before gc: %dmb%n",
                    memoryUsedInMB());
            // We make sure that the objects are all collected
            for (int i = 0; i < 10; i++) {
                System.gc();
                Thread.sleep(10);
            }
            // This should be 0 or close to that
            System.out.printf("UsedMem after gc: %dmb%n%n",
                    memoryUsedInMB());
        }
    }

    private static long memoryUsedInMB() {
        return (Runtime.getRuntime().totalMemory() -
                Runtime.getRuntime().freeMemory()) / 1024 / 1024;
    }

    private static class Zombie {
        // Since finalized is being accessed from multiple threads,
        // we need to make it volatile
        private volatile boolean finalized = false;

        protected void finalize() throws Throwable {
            if (finalized) System.err.println("Finalized twice");
            finalized = true;
            lruCache.put(this, this); // we let 'this' reference escape
            super.finalize();
        }
    }
}