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

package eu.javaspecialists.tjsn.examples.issue190;

import java.util.concurrent.locks.*;

public class AutoLockSimpleTest {
    private final static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        try (AutoLockSimple als = new AutoLockSimple(lock)) {
            printLockStatus();
        }
        printLockStatus();
    }

    private static void printLockStatus() {
        System.out.println("We are locked: " + lock.isHeldByCurrentThread());
    }
}
