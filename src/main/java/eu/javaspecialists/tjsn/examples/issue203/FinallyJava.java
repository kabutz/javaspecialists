/*
 * Copyright (C) 2000-2012 Heinz Max Kabutz
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

package eu.javaspecialists.tjsn.examples.issue203;

import java.util.concurrent.*;

/**
 * A slightly less obscure construct than the labelled break seen in
 * GoToJava.
 * <p/>
 * Demo class from http://www.javaspecialists.eu/archive/Issue203.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class FinallyJava {
    public void foo() {
        try {
            if (ThreadLocalRandom.current().nextBoolean()) {
                System.out.println("First random true");
                return;
            }
            if (ThreadLocalRandom.current().nextBoolean()) {
                System.out.println("Second random true");
                return;
            }
            System.out.println("Both randoms false");
        } finally {
            System.out.println("Done");
        }
    }

    public static void main(String[] args) {
        new FinallyJava().foo();
    }
}
