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

import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * Strategy to check that we really did have the call() method being invoked by
 * alternate threads.
 * <p/>
 * Described in http://www.javaspecialists.eu/archive/Issue188.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class InterleavedNumberTestingStrategy implements
    InterlockTask<VerifyResult> {
  public final int upto;
  private final Map<Integer, Thread> numbers =
      new LinkedHashMap<Integer, Thread>();
  private final AtomicInteger count = new AtomicInteger(0);

  public InterleavedNumberTestingStrategy(int upto) {
    this.upto = upto;
  }

  public boolean isDone() {
    return count.get() >= upto;
  }

  public void call() {
    int next = count.getAndIncrement();
    numbers.put(next, Thread.currentThread());
  }

  public VerifyResult get() {
    if (numbers.size() < upto) {
      return new VerifyResult("Only " + numbers.size() +
          " numbers were entered");
    }
    Object previous = null;
    int i = 0;
    for (Map.Entry<Integer, Thread> entry : numbers.entrySet()) {
      if (i != entry.getKey()) {
        return new VerifyResult("numbers out of sequence");
      }
      if (entry.getValue() == previous) {
        return new VerifyResult("Did not alternate threads");
      }
      previous = entry.getValue();
      i++;
    }
    Set<Thread> values = new HashSet<Thread>(numbers.values());
    if (values.size() != 2) {
      return new VerifyResult(
          "More than two threads were inserting values");
    }
    return new VerifyResult();
  }

  public void reset() {
    numbers.clear();
    count.set(0);
  }
}