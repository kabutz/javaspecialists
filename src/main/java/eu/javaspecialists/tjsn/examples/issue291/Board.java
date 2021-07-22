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
import java.util.stream.*;

public class Board {
    private final int[] snakes, ladders;

    // Internal helper methods

    /**
     * Flatten a Map<Integer, Integer> to an int[101]
     */
    private static int[] flatten(Map<Integer, Integer> map) {
        int[] result = new int[101];
        map.forEach((from, to) -> result[from] = to);
        return result;
    }

    /**
     * Expand an int[101] back into a Map<Integer, Integer>
     */
    private static Map<Integer, Integer> expand(int[] matrix) {
        Map<Integer, Integer> result = new HashMap<>();
        for (int from = 1; from < matrix.length; from++) {
            int to = matrix[from];
            if (to != 0) result.put(from, to);
        }
        return Map.copyOf(result);
    }

    /**
     * Reverse a Map so each jump goes in the opposite direction.
     */
    private static Map<Integer, Integer> reverse(
            Map<Integer, Integer> map) {
        return map.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getValue,
                Map.Entry::getKey
        ));
    }

    /**
     * Create a game board with map of snakes (sliding down) and
     * ladders (climbing up).
     */
    public Board(Map<Integer, Integer> snakes,
                 Map<Integer, Integer> ladders) {
        this.snakes = flatten(snakes);
        this.ladders = flatten(ladders);
    }

    /**
     * Create a new game board with the direction of snakes and
     * ladders reversed.
     */
    public Board reverse() {
        return new Board(reverse(expand(ladders)),
                reverse(expand(snakes)));
    }

    public void play(GameObserver observer) {
        play(observer, new DiceFair());
    }

    public void play(GameObserver observer, Dice dice) {
        int position = 1; // we start on position 1
        do {
            observer.turn();
            int roll;
            do {
                roll = dice.roll();
                observer.roll(roll);
                position = next(observer, position, roll);
            } while (roll == 6 && position != 100); // 6 rolls again
        } while (position != 100);
        observer.finished();
    }

    private int next(GameObserver observer, int pos, int count) {
        int next = pos + count;
        if (next > 100)
            next = 100 - (next % 100); // bounce off end
        int jump;
        if ((jump = snakes[next]) != 0 ||
                (jump = ladders[next]) != 0) {
            observer.jump(next, jump);
            next = jump;
        }
        return next;
    }
}