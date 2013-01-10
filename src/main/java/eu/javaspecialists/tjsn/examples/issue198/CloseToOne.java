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

package eu.javaspecialists.tjsn.examples.issue198;

/**
 * Demo class from http://www.javaspecialists.eu/archive/Issue198.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class CloseToOne {
    public static double makeDouble(long first, long second) {
        return ((first << 27) + second) / (double) (1L << 53);
    }

    public static void main(String[] args) {

        long first = (1 << 26) - 1;
        long second = (1 << 27) - 1;

        System.out.println(makeDouble(first, second));
        System.out.println((int) (makeDouble(first, second) + 1));

        second--;
        System.out.println(makeDouble(first, second));
        System.out.println((int) (makeDouble(first, second) + 1));

        System.out.println((((1L << 53) - 1)) / (double) (1L << 53));
    }
}