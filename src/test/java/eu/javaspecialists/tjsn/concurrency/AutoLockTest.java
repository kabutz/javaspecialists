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

package eu.javaspecialists.tjsn.concurrency;

import org.junit.*;

import java.util.concurrent.*;
import java.util.concurrent.locks.*;

import static org.junit.Assert.*;

public class AutoLockTest {
    @Test
    public void testSimpleLocking() {
        ReentrantLock lock = new ReentrantLock();
        assertFalse(lock.isHeldByCurrentThread());
        try (AutoLock al = AutoLock.lock(lock)) {
            assertTrue(lock.isHeldByCurrentThread());
        }
        assertFalse(lock.isHeldByCurrentThread());
    }

    @Test
    public void testSimpleLockingWithInterrupt() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        assertFalse(lock.isHeldByCurrentThread());
        try (AutoLock al = AutoLock.lockInterruptibly(lock)) {
            assertTrue(lock.isHeldByCurrentThread());
        }
        assertFalse(lock.isHeldByCurrentThread());
    }

    @Test
    public void testLockingWithException() {
        ReentrantLock lock = new ReentrantLock();
        assertFalse(lock.isHeldByCurrentThread());
        try {
            try (AutoLock al = AutoLock.lock(lock)) {
                assertTrue(lock.isHeldByCurrentThread());
                throw new OutOfMemoryError();
            }
        } catch (Throwable t) {
            assertFalse(lock.isHeldByCurrentThread());
        }
    }

    @Test
    public void testLockingWithInterrupt() throws InterruptedException {
        final ReentrantLock lock = new ReentrantLock();
        final BlockingQueue<Boolean> result = new LinkedBlockingQueue<>();
        assertFalse(lock.isHeldByCurrentThread());
        try (AutoLock al = AutoLock.lock(lock)) {
            assertTrue(lock.isHeldByCurrentThread());
            ExecutorService pool = Executors.newFixedThreadPool(1);
            Future<Void> future = pool.submit(new Callable<Void>() {
                public Void call() {
                    try (AutoLock al = AutoLock.lockInterruptibly(lock)) {
                        assertTrue(lock.isHeldByCurrentThread());
                    } catch (InterruptedException e) {
                        assertFalse(lock.isHeldByCurrentThread());
                        result.add(Boolean.TRUE);
                    } finally {
                        assertFalse(lock.isHeldByCurrentThread());
                    }
                    return null;
                }
            });
            Thread.sleep(10);
            future.cancel(true);
            assertTrue(result.poll(100, TimeUnit.MILLISECONDS));
        }
        assertFalse(lock.isHeldByCurrentThread());
    }
}
