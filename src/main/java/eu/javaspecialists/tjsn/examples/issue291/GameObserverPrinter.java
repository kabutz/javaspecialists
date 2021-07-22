/*
 * Copyright (C) 2021 Heinz Max Kabutz
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

package eu.javaspecialists.tjsn.examples.issue291;

public class GameObserverPrinter extends GameObserverSummary {
    private int rolls = 0;

    public void turn() {
        super.turn();
        System.out.println("Turn " + turns() + ":");
    }

    public void roll(int roll) {
        rolls++;
        System.out.println("Rolled a " + roll);
    }

    public void jump(int from, int to) {
        System.out.println("Jumped from " + from + " to " + to);
    }

    public void finished() {
        System.out.println("That took " + turns() + " turns and " +
                rolls + " rolls");
    }
}