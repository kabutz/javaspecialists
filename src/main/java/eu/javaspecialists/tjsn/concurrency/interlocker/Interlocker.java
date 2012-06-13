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

/**
 * This special executor guarantees that the call() method of the task
 * parameter
 * is invoked in turns by two threads.
 * <p/>
 * Described in http://www.javaspecialists.eu/archive/Issue188.html
 *
 * @author Dr Heinz M. Kabutz
 */
public abstract class Interlocker {
    protected abstract Runnable[] getRunnables(InterlockTask task);

    public final <T> T execute(InterlockTask<T> task)
            throws InterruptedException {
        Runnable[] jobs = getRunnables(task);
        Thread[] threads = new Thread[jobs.length];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(jobs[i]);
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        return task.get();
    }
}