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

package eu.javaspecialists.tjsn.examples.issue181.handcoded;

import eu.javaspecialists.tjsn.examples.issue181.*;

import java.util.concurrent.atomic.*;

public class VirtualMoralFibreLockFree
        extends VirtualMoralFibre {
    private final AtomicReference<MoralFibre> realSubject =
            new AtomicReference<>();

    protected MoralFibre realSubject() {
        MoralFibre subject = realSubject.get();
        if (subject == null) {
            subject = new MoralFibreImpl();
            if (!realSubject.compareAndSet(null, subject)) {
                subject = realSubject.get();
            }
        }
        return subject;
    }
}
