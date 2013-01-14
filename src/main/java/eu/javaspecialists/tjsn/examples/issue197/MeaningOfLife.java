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

package eu.javaspecialists.tjsn.examples.issue197;

public class MeaningOfLife {
    public static String findOutWhatLifeIsAllAbout() {
        int meaning = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 20; j++) {
                for (int k = 0; k < 300; k++) {
                    for (int m = 0; m < 7000; m++) {
                        meaning += Math.random() + 1;
                    }
                }
            }
        }
        return String.valueOf(meaning).replaceAll("0*$", "");
    }

    public static void main(String[] args) {
        System.out.println(findOutWhatLifeIsAllAbout());
    }
}