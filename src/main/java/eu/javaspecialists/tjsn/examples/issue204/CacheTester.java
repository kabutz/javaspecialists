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

package eu.javaspecialists.tjsn.examples.issue204;

public class CacheTester {
    private final int ARR_SIZE = 1 * 1024 * 1024;
    private final int[] arr = new int[ARR_SIZE];
    private static final int REPEATS = 1000;

    private void doLoop2() {
        for (int i = 0; i < arr.length; i++) arr[i]++;
    }

    private void doLoop1() {
        for (int i = 0; i < arr.length; i += 16) arr[i]++;
    }

    private void run() throws InterruptedException {
        for (int i = 0; i < 10000; i++) {
            doLoop1();
            doLoop2();
        }
        Thread.sleep(1000); // allow the hotspot compiler to work
        System.out.println("Loop1,Loop2");
        for (int i = 0; i < 100; i++) {
            long t0 = System.currentTimeMillis();
            for (int j = 0; j < REPEATS; j++) doLoop1();
            long t1 = System.currentTimeMillis();
            for (int j = 0; j < REPEATS; j++) doLoop2();
            long t2 = System.currentTimeMillis();
            long el = t1 - t0;
            long el2 = t2 - t1;
            System.out.println(el + "," + el2);
        }
    }

    public static void main(String[] args)
            throws InterruptedException {
        CacheTester ct = new CacheTester();
        ct.run();
    }
}