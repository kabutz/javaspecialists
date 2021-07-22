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

public class GameStarterTwoBoards {
    public static void main(String... args) {
        Board board1 = new EfiBoard();
        Board board2 = board1.reverse();

        int player1Wins = 0, player2Wins = 0;
        int bestTurns = Integer.MAX_VALUE;
        for (int i = 0; i < 10_000_000; i++) {
            var obs1 = new GameObserverSummary();
            var obs2 = new GameObserverSummary();
            board1.play(obs1);
            board2.play(obs2);
            if (obs1.turns() >= obs2.turns()) player1Wins++;
            else player2Wins++;
        }
        System.out.println("player1Wins = " + player1Wins);
        System.out.println("player2Wins = " + player2Wins);
        double diff = (player1Wins - player2Wins) / 100_000.0;
        System.out.printf(Locale.US, "diff = %.2f%%%n", diff);
    }
}