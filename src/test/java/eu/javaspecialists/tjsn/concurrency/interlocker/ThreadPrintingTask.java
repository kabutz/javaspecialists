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

package eu.javaspecialists.tjsn.concurrency.interlocker;

import java.util.concurrent.atomic.*;

/**
 * Prints the thread name that is invoking the call(), to allow us to manually
 * check that the interlocker is working as expected.
 * <p/>
 * Described in http://www.javaspecialists.eu/archive/Issue188.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class ThreadPrintingTask implements InterlockTask<Integer> {
  private final int upto;
  private AtomicInteger row = new AtomicInteger(0);

  public ThreadPrintingTask(int upto) {
    this.upto = upto;
  }

  public boolean isDone() {
    return row.get() >= upto;
  }

  public void call() {
    System.out.println(Thread.currentThread().getName());
    row.incrementAndGet();
  }

  public Integer get() {
    return upto;
  }

  public void reset() {
    row.set(0);
  }
}
