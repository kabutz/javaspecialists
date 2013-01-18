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

package eu.javaspecialists.tjsn.util.codegen;

import org.junit.*;

import java.util.concurrent.*;

import static org.junit.Assert.*;

public class GeneratorTest {
    @Test
    public void testClassGeneration()
            throws Exception {
        try {
            Class.forName("coolthings.WatchThis");
            fail("Class coolthings.WatchThis should not exist yet!");
        } catch (ClassNotFoundException expected) {
        }
        Class testClass = Generator.make(
                null, "WatchThis", "" +
                "package coolthings;\n" +
                "\n" +
                "import java.util.concurrent.Callable;\n" +
                "\n" +
                "public class WatchThis implements Callable<String> {\n" +
                "  public WatchThis() {\n" +
                "    System.out.println(\"Hey this works!\");\n" +
                "  }\n" +
                "\n" +
                "  public String call() {\n" +
                "    System.out.println(Thread.currentThread());\n" +
                "    while(Math.random() < 0.8) {\n" +
                "      System.out.println(\"Cool stuff!\");\n" +
                "    }\n" +
                "    return \"Amazing!\";\n" +
                "  }\n" +
                "}\n"
        );
        Callable<String> obj = (Callable<String>) testClass.newInstance();
        Class<?> clazz = obj.getClass();
        assertEquals("coolthings.WatchThis", clazz.getName());
        assertNull(clazz.getClassLoader());
        System.out.println("Our class: " + clazz.getName());
        System.out.println("Classloader: " + clazz.getClassLoader());
        assertEquals("Amazing!", obj.call());
        try {
            Class.forName("coolthings.WatchThis");
        } catch (ClassNotFoundException ex) {
            fail("Class coolthings.WatchThis should now exist! " + ex);
        }
    }
}
