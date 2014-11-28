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

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

/**
 * The ManagedReentrantLock is a lock implementation that is
 * compatible with the Fork/Join framework, and therefore also
 * with the Java 8 parallel streams.  Instead of just blocking
 * when the lock is held by another thread, and thereby removing
 * one of the active threads from the Fork/Join pool, we instead
 * use a ManagedBlocker to manage it.
 * <p>
 * The ManagedReentrantLock subclasses ReentrantLock, which means
 * we can use it as a drop-in replacement.  See also the
 * ManagedBlockers facade, which can adapt several known
 * synchronizers with our new ManagedReentrantLock.
 *
 * @author Heinz Kabutz
 * @see eu.javaspecialists.tjsn.concurrent.util.ManagedBlockers
 */
public class ManagedReentrantLock extends ReentrantLock {
    public ManagedReentrantLock() {
    }

    public ManagedReentrantLock(boolean fair) {
        super(fair);
    }

    public void lockInterruptibly() throws InterruptedException {
        ForkJoinPool.managedBlock(new DoLockInterruptibly());
    }

    public void lock() {
        DoLock locker = new DoLock(); // we want to create this
        // before passing it into the lambda, to prevent it from
        // being created again if the thread is interrupted for some
        // reason
        Interruptions.saveForLater(
                () -> ForkJoinPool.managedBlock(locker));
    }

    public boolean tryLock(long time, TimeUnit unit)
            throws InterruptedException {
        // If we already have the lock, then the TryLocker will
        // immediately acquire the lock due to reentrancy.  We do not
        // really care whether we had a timeout inside the TryLocker,
        // but only want to return whether or not we hold the lock
        // at the end of the method.
        ForkJoinPool.managedBlock(new TryLocker(time, unit));
        return isHeldByCurrentThread();
    }

    public Condition newCondition() {
        return new ManagedCondition(super.newCondition());
    }

    public boolean hasWaiters(Condition c) {
        return super.hasWaiters(getRealCondition(c));
    }

    public int getWaitQueueLength(Condition c) {
        return super.getWaitQueueLength(getRealCondition(c));
    }

    protected Collection<Thread> getWaitingThreads(Condition c) {
        return super.getWaitingThreads(getRealCondition(c));
    }

    ////// Helper functions and inner classes /////////

    private Condition getRealCondition(Condition c) {
        if (!(c instanceof ManagedCondition))
            throw new IllegalArgumentException("not owner");
        return ((ManagedCondition) c).condition;
    }

    private class ManagedCondition implements Condition {
        private final Condition condition;

        private ManagedCondition(Condition condition) {
            this.condition = condition;
        }

        public void await() throws InterruptedException {
            managedBlock(() -> condition.await());
        }

        public void awaitUninterruptibly() {
            Interruptions.saveForLater(
                    () -> managedBlock(
                            () -> condition.awaitUninterruptibly())
            );
        }

        public long awaitNanos(long nanosTimeout)
                throws InterruptedException {
            long[] result = {nanosTimeout};
            managedBlock(
                    () -> result[0] = condition.awaitNanos(nanosTimeout));
            return result[0];
        }

        public boolean await(long time, TimeUnit unit)
                throws InterruptedException {
            boolean[] result = {false};
            managedBlock(
                    () -> result[0] = condition.await(time, unit));
            return result[0];
        }

        public boolean awaitUntil(Date deadline)
                throws InterruptedException {
            boolean[] result = {false};
            managedBlock(
                    () -> result[0] = condition.awaitUntil(deadline));
            return result[0];
        }

        public void signal() {
            condition.signal();
        }

        public void signalAll() {
            condition.signalAll();
        }
    }

    private static void managedBlock(
            AlwaysBlockingManagedBlocker blocker)
            throws InterruptedException {
        ForkJoinPool.managedBlock(blocker);
    }

    // we should always try to achieve our goal within the
    // isReleasable() method instead of block().  This avoids
    // trying to compensate the loss of a thread by creating
    // a new one.
    private abstract class AbstractLockAction
            implements ForkJoinPool.ManagedBlocker {
        private boolean hasLock = false;

        public final boolean isReleasable() {
            return hasLock || (hasLock = tryLock());
        }
    }

    private class DoLockInterruptibly extends AbstractLockAction {
        public boolean block() throws InterruptedException {
            if (isReleasable()) return true;
            ManagedReentrantLock.super.lockInterruptibly();
            return true;
        }
    }

    private class DoLock extends AbstractLockAction {
        public boolean block() {
            if (isReleasable()) return true;
            ManagedReentrantLock.super.lock();
            return true;
        }
    }

    private class TryLocker extends AbstractLockAction {
        private final long time;
        private final TimeUnit unit;

        private TryLocker(long time, TimeUnit unit) {
            this.time = time;
            this.unit = unit;
        }

        public boolean block() throws InterruptedException {
            if (isReleasable()) return true;
            ManagedReentrantLock.super.tryLock(time, unit);
            return true;
        }
    }

    @FunctionalInterface
    private static interface AlwaysBlockingManagedBlocker
            extends ForkJoinPool.ManagedBlocker {
        default boolean isReleasable() {
            return false;
        }

        default boolean block() throws InterruptedException {
            doBlock();
            return true;
        }

        void doBlock() throws InterruptedException;
    }
}
