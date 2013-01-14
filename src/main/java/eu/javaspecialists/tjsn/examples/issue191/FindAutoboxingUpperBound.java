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

package eu.javaspecialists.tjsn.examples.issue191;

/**
 * @author Heinz Kabutz
 */
public class FindAutoboxingUpperBound {
    public static void main(String[] args) {
        int i = 0;
        while (isSame(i, i)) {
            i++;
        }
        System.out.println("Upper bound is " + (i - 1));
        i = 0;
        while (isSame(i, i)) {
            i--;
        }
        System.out.println("Lower bound is " + (i + 1));
    }

    private static boolean isSame(Integer i0, Integer i1) {
        return i0 == i1;
    }
}