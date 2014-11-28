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

package eu.javaspecialists.tjsn.concurrency.locks;

import eu.javaspecialists.tjsn.concurrency.util.*;
import org.junit.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.stream.*;

import static org.junit.Assert.*;

public class ManagedReentrantLockTest {
    @Test
    public void testLockFunctionality() throws InterruptedException {
        Collection<ReentrantLock> locks = Arrays.asList(
                new ReentrantLock(),
                new ManagedReentrantLock());

        locks.forEach(l -> lockUnlock(l));
        locks.forEach(l -> lockUnlockInterruptibly(l));
        locks.forEach(l -> tryLockUnlock(l));
        locks.forEach(l -> reentrantLockUnlock(l));
        locks.forEach(l -> interruptions(l));
    }

    private volatile boolean runningOther = true;
    private void interruptions(ReentrantLock lock) {
        Thread.currentThread().interrupt();
        assertTrue(Thread.currentThread().isInterrupted());
        lock.lock();
        assertTrue(Thread.currentThread().isInterrupted());
        lock.unlock();
        assertTrue(Thread.interrupted());

        lock.lock();

        Thread other = new Thread(() -> {
            try {
                lock.lock(); // will block for a while
                lock.unlock();
            } catch(Throwable t) {
                t.printStackTrace();
            }
            while(runningOther) {}
        });
        other.start();
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(100));
        other.interrupt();
        lock.unlock();
        assertTrue(other.isInterrupted());
        runningOther = false;
    }

    private void reentrantLockUnlock(ReentrantLock lock) {
        assertFalse(lock.isHeldByCurrentThread());
        lock.lock();
        try {
            assertTrue(lock.isHeldByCurrentThread());
            lock.lock();
            try {
                assertEquals(2, lock.getHoldCount());
                assertTrue(lock.isHeldByCurrentThread());
            } finally {
                lock.unlock();
            }
        } finally {
            lock.unlock();
        }
        assertFalse(lock.isHeldByCurrentThread());
    }

    private void lockUnlock(ReentrantLock lock) {
        assertFalse(lock.isHeldByCurrentThread());
        lock.lock();
        try {
            assertTrue(lock.isHeldByCurrentThread());
        } finally {
            lock.unlock();
        }
        assertFalse(lock.isHeldByCurrentThread());
    }

    private void lockUnlockInterruptibly(ReentrantLock lock) {
        try {
            assertFalse(lock.isHeldByCurrentThread());
            lock.lockInterruptibly();
            try {
                assertTrue(lock.isHeldByCurrentThread());
            } finally {
                lock.unlock();
            }
            assertFalse(lock.isHeldByCurrentThread());
        } catch (InterruptedException e) {
            fail("Did not expect an interrupted exception here");
        }
    }

    private void tryLockUnlock(ReentrantLock lock) {
        assertFalse(lock.isHeldByCurrentThread());
        assertTrue(lock.tryLock());
        try {
            assertTrue(lock.isHeldByCurrentThread());
        } finally {
            lock.unlock();
        }
        assertFalse(lock.isHeldByCurrentThread());
    }


    @Test
    public void testBlockingLockingOperation() throws InterruptedException {
        int processors = Runtime.getRuntime().availableProcessors();

        Lock lock = new ManagedReentrantLock();
        lock.lock();

        Thread jamup = new Thread(() -> {
            int par = processors * 2;
            IntStream.range(0, par).parallel().forEach(
                    i -> {
                        System.out.println("Waiting for lock");
                        lock.lock();
                        try {
                            System.out.println("Acquired lock");
                        } finally {
                            lock.unlock();
                        }
                    }
            );
        });
        jamup.start();
        Thread.sleep(100);
        System.out.println("pool size: " + ForkJoinPool.commonPool().getPoolSize());

        IntStream.range(0, processors).parallel().forEach(
                i -> {
                    System.out.println("Going to sleep: " + Thread.currentThread() + " " + i);
                    Interruptions.saveForLater(() -> Thread.sleep(1000));
                    System.out.println("Done: " + Thread.currentThread() + " " + i);
                });
        System.out.println("pool size: " + ForkJoinPool.commonPool().getPoolSize());
        lock.unlock();
        jamup.join();
    }

    @Test
    public void testBlockingOperation() throws InterruptedException {
        int processors = Runtime.getRuntime().availableProcessors();

        Lock lock = new ManagedReentrantLock();
        Condition condition = lock.newCondition();
        Thread jamup = new Thread(() -> {
            int par = processors * 2;
            IntStream.range(0, par).parallel().forEach(
                    i -> {
                        lock.lock();
                        try {
                            System.out.println("Waiting");
                            condition.awaitUninterruptibly();
                            System.out.println("Finished waiting");
                        } finally {
                            lock.unlock();
                        }
                    }
            );
        });
        jamup.start();
        Thread.sleep(100);
        System.out.println("pool size: " + ForkJoinPool.commonPool().getPoolSize());

        IntStream.range(0, processors).parallel().forEach(
                i -> {
                    System.out.println("Going to sleep: " + Thread.currentThread() + " " + i);
                    Interruptions.saveForLater(() -> Thread.sleep(1000));
                    System.out.println("Done: " + Thread.currentThread() + " " + i);
                });
        System.out.println("pool size: " + ForkJoinPool.commonPool().getPoolSize());
        lock.lock();
        try {
            condition.signalAll();
        } finally {
            lock.unlock();
        }
        jamup.join();
    }

    @Test
    public void testArrayBlockingQueue() {
        ArrayBlockingQueue<Integer> queue =
                ManagedBlockers.makeManaged(
                        new ArrayBlockingQueue<Integer>(10));

        int jobs = Runtime.getRuntime().availableProcessors() * 10;
        Thread t = new Thread(() -> {
            for (int i = 0; i < jobs; i++) {
                queue.add(i);
                Interruptions.saveForLater(() -> Thread.sleep(100));
            }
        });
        t.start();

        IntStream.range(0, jobs).parallel().forEach(
                i -> {
                    try {
                        System.out.println("Waiting to take an element");
                        System.out.println(i + queue.take());
                    } catch (InterruptedException e) {
                        System.err.println(e);
                    }
                }
        );
        System.out.println("forEach done");
    }

    @Test
    public void testLinkedBlockingQueue() {
        BlockingQueue<Integer> queue =
                ManagedBlockers.makeManaged(
                        new LinkedBlockingQueue<Integer>(10)
                );

        int jobs = Runtime.getRuntime().availableProcessors() * 10;
        Thread t = new Thread(() -> {
            for (int i = 0; i < jobs; i++) {
                queue.add(i);
                Interruptions.saveForLater(() -> Thread.sleep(100));
            }
        });
        t.start();

        IntStream.range(0, jobs).parallel().forEach(
                i -> {
                    try {
                        System.out.println("Waiting to take an element");
                        System.out.println(i + queue.take());
                    } catch (InterruptedException e) {
                        System.err.println(e);
                    }
                }
        );
        System.out.println("forEach done");
    }

    @Test
    public void testHasWaiters() throws InterruptedException {
        testHasWaiters(new ReentrantLock());
        testHasWaiters(new ManagedReentrantLock());
    }

    private void testHasWaiters(ReentrantLock lock)
            throws InterruptedException {
        Condition condition = lock.newCondition();
        lock.lock();
        try {
            assertFalse(lock.hasWaiters(condition));
            assertEquals(0, lock.getWaitQueueLength(condition));
        } finally {
            lock.unlock();
        }

        Thread t = new Thread(() -> {
            lock.lock();
            try {
                condition.awaitUninterruptibly();
            } finally {
                lock.unlock();
            }
        });
        t.start();
        Thread.sleep(100); // wait for t to wait

        lock.lock();
        try {
            assertTrue(lock.hasWaiters(condition));
            assertEquals(1, lock.getWaitQueueLength(condition));
        } finally {
            lock.unlock();
        }
    }

    @Test
    public void testQueuedThread() throws InterruptedException {
        testQueuedThread(new ReentrantLock());
        testQueuedThread(new ManagedReentrantLock());
    }

    private void testQueuedThread(ReentrantLock lock)
            throws InterruptedException {
        lock.lock();
        try {
            assertFalse(lock.hasQueuedThread(Thread.currentThread()));

            Thread t = new Thread(() -> {
                lock.lock();
                try {
                } finally {
                    lock.unlock();
                }
            });
            t.start();
            Thread.sleep(100); // wait for t to block on lock()

            assertTrue(lock.hasQueuedThread(t));

        } finally {
            lock.unlock();
        }
    }

    @Test
    public void testToString() {
        System.out.println(new ReentrantLock());
        System.out.println(new ReentrantLock(true));
        System.out.println(new ManagedReentrantLock());
        System.out.println(new ManagedReentrantLock(true));
    }
}

