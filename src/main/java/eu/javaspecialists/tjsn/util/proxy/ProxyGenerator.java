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

package eu.javaspecialists.tjsn.util.proxy;

import eu.javaspecialists.tjsn.util.codegen.*;

import java.lang.reflect.*;
import java.util.*;

public class ProxyGenerator {
    private static final WeakHashMap<ClassLoader, Map<CacheKey, Class<?>>> cache =
            new WeakHashMap<>();

    public static <T> T make(
            Class<T> subject,
            Class<? extends T> realClass,
            Concurrency concurrency,
            ProxyType type) {
        return make(subject.getClassLoader(),
                subject, realClass, concurrency, type);
    }

    public static <T> T make(
            Class<T> subject, Class<? extends T> realClass,
            Concurrency concurrency) {
        return make(subject, realClass, concurrency,
                ProxyType.STATIC);
    }

    public static <T> T make(ClassLoader loader,
                             Class<T> subject,
                             Class<? extends T> realClass,
                             Concurrency concurrency,
                             ProxyType type) {

        Object proxy = null;
        if (type == ProxyType.STATIC) {
            proxy = createStaticProxy(loader, subject,
                    realClass, concurrency);
        } else if (type == ProxyType.DYNAMIC) {
            proxy = createDynamicProxy(loader,
                    subject, realClass, concurrency);
        }
        return subject.cast(proxy);
    }

    private static Object createStaticProxy(
            ClassLoader loader, Class subject,
            Class realClass, Concurrency concurrency) {
        Map<CacheKey, Class<?>> clcache;
        synchronized (cache) {
            clcache = cache.get(loader);
            if (clcache == null) {
                cache.put(loader, clcache = new HashMap<>());
            }
        }

        try {
            Class clazz;
            CacheKey key = new CacheKey(subject, concurrency);
            synchronized (clcache) {
                clazz = (Class) clcache.get(key);
                if (clazz == null) {
                    VirtualProxySourceGenerator vpsg = create(subject,
                            realClass, concurrency);
                    clazz = Generator.make(loader, vpsg.getProxyName(),
                            vpsg.getCharSequence());
                    clcache.put(key, clazz);
                }
            }
            return clazz.newInstance();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static VirtualProxySourceGenerator create(
            Class subject, Class realClass,
            Concurrency concurrency) {
        switch (concurrency) {
            case NONE:
                return new VirtualProxySourceGeneratorNotThreadsafe(
                        subject, realClass
                );
            case SOME_DUPLICATES:
                return new VirtualProxySourceGeneratorSomeDuplicates(
                        subject, realClass
                );
            case NO_DUPLICATES:
                return new VirtualProxySourceGeneratorNoDuplicates(
                        subject, realClass
                );
            default:
                throw new IllegalArgumentException(
                        "Unsupported Concurrency: " + concurrency);
        }
    }

    private static Object createDynamicProxy(
            ClassLoader loader, Class subject,
            Class realClass, Concurrency concurrency) {
        if (concurrency != Concurrency.NONE) {
            throw new IllegalArgumentException(
                    "Unsupported Concurrency: " + concurrency);
        }
        return Proxy.newProxyInstance(
                loader,
                new Class<?>[]{subject},
                new VirtualDynamicProxyNotThreadSafe(realClass));
    }

    private static class CacheKey {
        private final Class subject;
        private final Concurrency concurrency;

        private CacheKey(Class subject, Concurrency concurrency) {
            this.subject = subject;
            this.concurrency = concurrency;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CacheKey that = (CacheKey) o;
            if (concurrency != that.concurrency) return false;
            return subject.equals(that.subject);
        }

        public int hashCode() {
            return 31 * subject.hashCode() + concurrency.hashCode();
        }
    }
}
