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

package eu.javaspecialists.tjsn.examples.issue184;

import java.io.*;
import java.util.*;

public class MangledSynchronizedList {
    public static void main(String[] args) {
        final List<String> synchList = Collections.synchronizedList(
                new ArrayList<String>());
        Collections.addAll(synchList, "hello", "world");
        Thread tester = new Thread() {
            {
                setDaemon(true);
            }

            public void run() {
                while (true) {
                    synchList.add("hey there");
                    synchList.remove(2);
                }

            }
        };
        tester.start();

        while (true) {
            try {
                ObjectOutputStream out = new ObjectOutputStream(
                        new NullOutputStream()
                );
                for (int i = 0; i < 100 * 1000; i++) {
                    out.writeObject(synchList);
                }
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}