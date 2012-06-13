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

package eu.javaspecialists.tjsn.concurrency.interlocker.impl;

import eu.javaspecialists.tjsn.concurrency.interlocker.*;

import java.util.concurrent.*;

/**
 * The easiest solution is using Semaphores.  It can also be expanded to work
 * with more than two threads.
 * <p/>
 * Described in http://www.javaspecialists.eu/archive/Issue188.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class SemaphoreInterlocker extends Interlocker {
    private static class Job implements Runnable {
        private final InterlockTask task;
        private final Semaphore first;
        private final Semaphore second;

        public Job(InterlockTask task,
                   Semaphore first, Semaphore second) {
            this.task = task;
            this.first = first;
            this.second = second;
        }

        public void run() {
            while (!task.isDone()) {
                first.acquireUninterruptibly();
                if (task.isDone()) return;
                task.call();
                second.release();
            }
        }
    }

    protected Runnable[] getRunnables(InterlockTask task) {
        Semaphore even = new Semaphore(1);
        Semaphore odd = new Semaphore(0);
        return new Runnable[]{
                new Job(task, even, odd),
                new Job(task, odd, even)
        };
    }
}