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
package eu.javaspecialists.tjsn.examples.issue194;

import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class ThinkerTryLock implements Callable<String> {
    private final int id;
    private final Lock left;
    private final Lock right;

    public ThinkerTryLock(int id, Lock left, Lock right) {
        this.id = id;
        this.left = left;
        this.right = right;
    }

    public String call() throws Exception {
        for (int i = 0; i < 1000; i++) {
            drink(1, TimeUnit.SECONDS);
            think();
        }
        return "Java is fun";
    }

    public void drink(long timeout, TimeUnit unit)
            throws InterruptedException, TimeoutException {
        long timeoutInNanos = unit.toNanos(timeout);
        long stopTime = System.nanoTime() + timeoutInNanos;
        long sleepTime = ThreadLocalRandom.current().nextLong(
                100, timeoutInNanos / 50);

        while (true) {
            if (Thread.interrupted()) throw new InterruptedException();
            if (left.tryLock()) {
                try {
                    if (right.tryLock()) {
                        try {
                            System.out.printf("(%d) Drinking%n", id);
                            return;
                        } finally {
                            right.unlock();
                        }
                    }
                } finally {
                    left.unlock();
                }
            }
            if (System.nanoTime() > stopTime)
                throw new TimeoutException();
            TimeUnit.NANOSECONDS.sleep(sleepTime);
        }
    }

    public void think() {
        System.out.printf("(%d) Thinking%n", id);
    }
}