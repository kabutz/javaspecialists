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

/**
 * This interlocker uses the classical wait/notify approach for passing the
 * baton between threads.
 * <p/>
 * Described in http://www.javaspecialists.eu/archive/Issue188.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class WaitNotifyInterlocker extends Interlocker {
  private boolean evenHasNextTurn = true;
  private final Object lock = new Object();

  private class Job implements Runnable {
    private final InterlockTask task;
    private final boolean even;

    Job(InterlockTask task, boolean even) {
      this.task = task;
      this.even = even;
    }

    public void run() {
      try {
        while (!task.isDone()) {
          synchronized (lock) {
            while (!task.isDone() && (even ^ evenHasNextTurn)) {
              lock.wait();
            }
            if (task.isDone()) return;
            task.call();
            evenHasNextTurn = !evenHasNextTurn;
            lock.notify();
          }
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  protected Runnable[] getRunnables(InterlockTask task) {
    return new Runnable[]{
        new Job(task, true),
        new Job(task, false)
    };
  }
}
