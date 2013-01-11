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

import java.lang.management.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

import static org.junit.Assert.*;

public class AutoLockTest {
    private static final MemoryMXBean memoryMXBean =
            ManagementFactory.getMemoryMXBean();

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

    @Test
    public void testDowngrading() {
        ReadWriteLock rwlock = new ReentrantReadWriteLock();

        try (AutoLock al = AutoLock.write(rwlock)) {
            try (AutoLock al2 = AutoLock.read(rwlock)) {

            }
        }
    }

    @Test
    public void testDowngradingRaw() {
        ReadWriteLock rwlock = new ReentrantReadWriteLock();
        rwlock.writeLock().lock();
        try {
            rwlock.readLock().lock();
            try {
                //
            } finally {
                rwlock.readLock().unlock();
            }
        } finally {
            rwlock.writeLock().unlock();
        }
    }

    @Test
    public void testDowngradingNested() {
        ReadWriteLock rwlock = new ReentrantReadWriteLock();
        try (AutoLock al = AutoLock.write(rwlock)) {
            try (AutoLock al2 = AutoLock.read(rwlock)) {
                try (AutoLock al3 = AutoLock.write(rwlock)) {

                }
            }
        }
    }

    @Test
    public void testDowngradingNestedRaw() {
        ReadWriteLock rwlock = new ReentrantReadWriteLock();
        rwlock.writeLock().lock();
        try {
            rwlock.readLock().lock();
            try {
                rwlock.writeLock().lock();
                try {

                } finally {
                    rwlock.writeLock().unlock();
                }
            } finally {
                rwlock.readLock().unlock();
            }
        } finally {
            rwlock.writeLock().unlock();
        }
    }

    @Test(expected = IllegalMonitorStateException.class)
    public void testUpgrading() {
        ReadWriteLock rwlock = new ReentrantReadWriteLock();

        try (AutoLock al = AutoLock.read(rwlock)) {
            try (AutoLock al2 = AutoLock.write(rwlock)) {

            }
        }
    }

    private volatile boolean running = false;

    @Test
    public void testSingleThreadedPerformance() {
        Lock lock = new ReentrantLock();
        testSingleThreadedPerformance(lock, 2);

        boolean verboseEnabled = memoryMXBean.isVerbose();
        memoryMXBean.setVerbose(true);
        int gcs = getTotalGCs();
        testSingleThreadedPerformance(lock, 5);
        gcs = getTotalGCs() - gcs;
        assertEquals("AutoLock objects should be elided with Escape Analysis",
                0, gcs);
        memoryMXBean.setVerbose(verboseEnabled);
    }

    private void testSingleThreadedPerformance(Lock lock, int repeats) {
        for (int i = 0; i < repeats; i++) {
            cancelInOneSecond();
            System.out.println(testLocking(lock));
            cancelInOneSecond();
            System.out.println(testAutoLocking(lock));
        }
    }

    @Test
    public void testMultiThreadedPerformance() throws InterruptedException, ExecutionException {
        int THREADS = 4;

        boolean verboseEnabled = memoryMXBean.isVerbose();
        memoryMXBean.setVerbose(true);
        final Lock lock = new ReentrantLock();

        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        Collection<Future<Long>> futures = new ArrayList<>(THREADS);
        long totalLocking = 0;
        long totalAutoLocking = 0;
        for (int i = 0; i < 5; i++) {
            running = true;
            futures.clear();
            for (int j = 0; j < THREADS; j++) {
                futures.add(pool.submit(new Callable<Long>() {
                    public Long call() throws Exception {
                        return testLocking(lock);
                    }
                }));
            }
            Thread.sleep(1000);
            running = false;
            long add0 = add(futures);
            System.out.println("add0 = " + add0);
            totalLocking += add0;

            running = true;
            futures.clear();
            for (int j = 0; j < THREADS; j++) {
                futures.add(pool.submit(new Callable<Long>() {
                    public Long call() throws Exception {
                        return testAutoLocking(lock);
                    }
                }));
            }
            Thread.sleep(1000);
            running = false;
            long add1 = add(futures);
            System.out.println("add1 = " + add1);
            totalAutoLocking += add1;
        }
        pool.shutdown();
        while (!pool.awaitTermination(1, TimeUnit.SECONDS)) ;

        System.out.println("totalLocking = " + totalLocking);
        System.out.println("totalAutoLocking = " + totalAutoLocking);
        memoryMXBean.setVerbose(verboseEnabled);
    }

    private long add(Collection<Future<Long>> futures)
            throws InterruptedException, ExecutionException {
        long total = 0;
        for (Future<Long> future : futures) {
            total += future.get();
        }
        return total;
    }

    private long testLocking(Lock lock) {
        long numberOfLocks = 0;
        while (running) {
            lockUnlock(lock);
            numberOfLocks++;
        }
        return numberOfLocks;
    }

    private void lockUnlock(Lock lock) {
        lock.lock();
        try {
        } finally {
            lock.unlock();
        }
    }

    private long testAutoLocking(Lock lock) {
        long numberOfLocks = 0;
        while (running) {
            lockAutoUnlock(lock);
            numberOfLocks++;
        }
        return numberOfLocks;
    }

    private void lockAutoUnlock(Lock lock) {
        try (AutoLock al = AutoLock.lock(lock)) {
        }
    }

    private void cancelInOneSecond() {
        final Timer timer = new Timer("test timer");
        TimerTask task = new TimerTask() {
            public void run() {
                running = false;
                timer.cancel();
            }
        };
        timer.schedule(task, 1000);
        running = true;
    }

    public int getTotalGCs() {
        int gcs = 0;
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            gcs += gcBean.getCollectionCount();
        }
        return gcs;
    }
}
