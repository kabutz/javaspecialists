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

package eu.javaspecialists.tjsn.concurrency.util;

import eu.javaspecialists.tjsn.concurrency.locks.*;

import java.lang.reflect.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class ManagedBlockers {
    public static <E> ArrayBlockingQueue<E> makeManaged(
            ArrayBlockingQueue<E> queue) {
        Class<?> clazz = ArrayBlockingQueue.class;

        try {
            Field lockField = clazz.getDeclaredField("lock");
            lockField.setAccessible(true);
            ReentrantLock old = (ReentrantLock) lockField.get(queue);
            boolean fair = old.isFair();
            ReentrantLock lock = new ManagedReentrantLock(fair);
            lockField.set(queue, lock);

            replace(queue, clazz, "notEmpty", lock.newCondition());
            replace(queue, clazz, "notFull", lock.newCondition());

            return queue;
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <E> LinkedBlockingQueue<E> makeManaged(
            LinkedBlockingQueue<E> queue) {
        Class<?> clazz = LinkedBlockingQueue.class;

        ReentrantLock takeLock = new ManagedReentrantLock();
        ReentrantLock putLock = new ManagedReentrantLock();

        try {
            replace(queue, clazz, "takeLock", takeLock);
            replace(queue, clazz, "notEmpty", takeLock.newCondition());
            replace(queue, clazz, "putLock", putLock);
            replace(queue, clazz, "notFull", putLock.newCondition());

            return queue;
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <E> PriorityBlockingQueue<E> makeManaged(
            PriorityBlockingQueue<E> queue) {
        Class<?> clazz = PriorityBlockingQueue.class;

        ReentrantLock lock = new ManagedReentrantLock();

        try {
            replace(queue, clazz, "lock", lock);
            replace(queue, clazz, "notEmpty", lock.newCondition());

            return queue;
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void replace(Object owner,
                                Class<?> clazz, String fieldName,
                                Object fieldValue)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(owner, fieldValue);
    }
}
