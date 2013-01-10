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

package eu.javaspecialists.tjsn.examples.issue193;

import eu.javaspecialists.tjsn.examples.issue193.objectfactories.*;
import eu.javaspecialists.tjsn.memory.*;

public class MemoryUsage {
    public static void main(String[] args) {
        MemoryTestBench mtb = new MemoryTestBench();
        mtb.showMemoryUsage(new BasicObjectFactory());
        mtb.showMemoryUsage(new LongObjectFactory());
        mtb.showMemoryUsage(new IntegerObjectFactory());

        mtb.showMemoryUsage(new HashMapFactory());
        mtb.showMemoryUsage(new SynchronizedHashMapFactory());
        mtb.showMemoryUsage(new HashtableFactory());

        mtb.showMemoryUsage(new ReentrantLockFactory());
        mtb.showMemoryUsage(new ConcurrentHashMapFactory());
        mtb.showMemoryUsage(new SmallConcurrentHashMapFactory());
        mtb.showMemoryUsage(new BigConcurrentHashMapFactory());
        mtb.showMemoryUsage(new HugeConcurrentHashMapFactory());
        mtb.showMemoryUsage(new HugeConcurrentHashMapV8Factory());

        mtb.showMemoryUsage(new HighlyScalableTableFactory());
    }
}