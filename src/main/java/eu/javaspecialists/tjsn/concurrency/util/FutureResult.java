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

package eu.javaspecialists.tjsn.concurrency.util;

/**
 * A  class maintaining a single reference variable serving as the result
 * of an operation. The result cannot be accessed until it has been set.
 * <p/>
 * It is based on Doug Lea's EDU.oswego.cs.dl.util.concurrent.FutureResult.
 *
 * @author Joe Bowbeer, Dr Heinz M. Kabutz
 */

public class FutureResult<V> {
    private V value;

    private boolean ready;

    public synchronized V get() throws InterruptedException {
        while (!ready) {
            wait();
        }
        return value;
    }

    public synchronized void set(V newValue) {
        value = newValue;
        ready = true;
        notifyAll();
    }

    public synchronized boolean isReady() {
        return ready;
    }
}