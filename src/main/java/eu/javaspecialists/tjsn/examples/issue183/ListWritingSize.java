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

import java.io.*;
import java.util.*;

public class ListWritingSize {
    public static void main(String[] args) throws IOException {
        test(new LinkedList<String>());
        test(new ArrayList<String>());
    }

    public static int test(List<String> list) throws IOException {
        for (int i = 0; i < 10; i++) {
            list.add("hello world");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(list);
        out.close();
        byte[] bytes = baos.toByteArray();
        System.out.println(list.getClass().getSimpleName() +
                " used " + bytes.length + " bytes : " + format(bytes));
        return bytes.length;
    }

    private static String format(byte[] bytes) {
        String result = new String(bytes);
        result = result.replaceAll("\n", "\\n");
        result = result.replaceAll("\r", "\\r");
        return result;
    }
}