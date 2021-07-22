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

public class TournamentObserver {
    private final List<Integer> turns = new ArrayList<>();
    private final List<Integer> climbs = new ArrayList<>();
    private final List<Integer> slides = new ArrayList<>();
    private int bestTurns = Integer.MAX_VALUE;
    private Collection<Integer> bestGame;

    public void play(Board board, Dice dice) {
        board.play(new GameObserverDetailed() {
            public void finished() {
                int turns = turns();
                if (turns < bestTurns) {
                    bestTurns = turns;
                    bestGame = rollHistory();
                }
                TournamentObserver.this.turns.add(turns);
                TournamentObserver.this.climbs.add(climbs());
                TournamentObserver.this.slides.add(slides());
            }
        }, dice);
    }

    public String toString() {
        IntSummaryStatistics turnsStats = turns.stream()
                .mapToInt(Integer::intValue)
                .summaryStatistics();
        double climbsAverage = climbs.stream()
                .mapToInt(Integer::intValue)
                .average().orElse(0.0);
        double slidesAverage = slides.stream()
                .mapToInt(Integer::intValue)
                .average().orElse(0.0);
        return String.format(Locale.US,
                "games={count=%,d, min=%d, average=%.1f, max=%d}, " +
                        "climbsAverage=%.1f, " +
                        "slidesAverage=%.1f, " +
                        "bestTurns=%d, bestGame=%s",
                turnsStats.getCount(),
                turnsStats.getMin(),
                turnsStats.getAverage(),
                turnsStats.getMax(),
                climbsAverage,
                slidesAverage,
                bestTurns,
                bestGame);
    }
}