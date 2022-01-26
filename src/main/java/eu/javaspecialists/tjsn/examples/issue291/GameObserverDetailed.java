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

import java.util.*;

public class GameObserverDetailed extends GameObserverSummary {
    private final List<Integer> rollHistory = new ArrayList<>();
    private int climbs, slides;

    public List<Integer> rollHistory() {
        return List.copyOf(rollHistory);
    }

    public void roll(int roll) {
        rollHistory.add(roll);
    }

    public void jump(int from, int to) {
        if (from < to) climbs += to - from;
        else slides += from - to;
    }

    public int climbs() {
        return climbs;
    }

    public int slides() {
        return slides;
    }
}