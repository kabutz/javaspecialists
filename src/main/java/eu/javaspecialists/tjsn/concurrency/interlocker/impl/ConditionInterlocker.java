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

package eu.javaspecialists.tjsn.concurrency.interlocker.impl;

import eu.javaspecialists.tjsn.concurrency.interlocker.*;

import java.util.concurrent.locks.*;

/**
 * This interlocker uses condition queues for passing the baton between
 * threads.
 * <p/>
 * Described in http://www.javaspecialists.eu/archive/Issue188.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class ConditionInterlocker extends Interlocker {
    // does not have to be volatile as all access is lock protected
    private boolean evenHasNextTurn = true;

    private class Job implements Runnable {
        private final InterlockTask task;
        private final boolean even;
        private final Lock lock;
        private final Condition firstCondition;
        private final Condition secondCondition;

        Job(InterlockTask task, boolean even, Lock lock,
            Condition firstCondition, Condition secondCondition) {
            this.task = task;
            this.even = even;
            this.lock = lock;
            this.firstCondition = firstCondition;
            this.secondCondition = secondCondition;
        }

        public void run() {
            try {
                while (!task.isDone()) {
                    lock.lock();
                    try {
                        while (!task.isDone() && (even ^ evenHasNextTurn)) {
                            firstCondition.await();
                        }
                        if (task.isDone()) return;
                        task.call();
                        evenHasNextTurn = !evenHasNextTurn;
                        secondCondition.signal();
                    } finally {
                        lock.unlock();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    protected Runnable[] getRunnables(InterlockTask task) {
        Lock lock = new ReentrantLock();
        Condition even_condition = lock.newCondition();
        Condition odd_condition = lock.newCondition();

        return new Runnable[]{
                new Job(task, true, lock, even_condition, odd_condition),
                new Job(task, false, lock, odd_condition, even_condition)
        };
    }
}
