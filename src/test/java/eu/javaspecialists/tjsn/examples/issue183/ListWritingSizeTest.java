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

package eu.javaspecialists.tjsn.examples.issue183;

import org.junit.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.Assert.*;

/**
 * Unit tests to find out early if anything changes
 */
public class ListWritingSizeTest {
    @Test
    public void testArrayListSize() throws IOException {
        assertEquals(117, ListWritingSize.test(new ArrayList<String>()));
    }

    @Test
    public void testLinkedListSize() throws IOException {
        assertEquals(107, ListWritingSize.test(new LinkedList<String>()));
    }

    @Test
    public void testStackSize() throws IOException {
        assertEquals(246, ListWritingSize.test(new Stack<String>()));
    }

    @Test
    public void testVectorSize() throws IOException {
        assertEquals(216, ListWritingSize.test(new Vector<String>()));
    }

    @Test
    public void testCopyOnWriteArrayListSize() throws IOException {
        assertEquals(128,
                ListWritingSize.test(new CopyOnWriteArrayList<String>()));
    }
}
