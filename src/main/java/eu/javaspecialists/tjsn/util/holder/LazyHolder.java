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

package eu.javaspecialists.tjsn.util.holder;

public abstract class LazyHolder<E> {
    private final Factory<E> factory;

    protected LazyHolder(Factory<E> factory) {
        this.factory = factory;
    }

    public abstract E get();

    protected final E makeNew() {
        return factory.make();
    }

    public static <E> LazyHolder<E> make(Concurrency level,
                                         Factory<E> factory) {
        switch (level) {
            case NOT_THREAD_SAFE:
                return new LazyHolderNotThreadSafe<>(factory);
            case THREAD_SAFE_LOCKING:
                return new LazyHolderThreadSafe<>(factory);
            case THREAD_SAFE_LOCK_FREE:
                return new LazyHolderLockFree<>(factory);
            default:
                throw new IllegalArgumentException("Unknown level: " + level);
        }
    }
}
