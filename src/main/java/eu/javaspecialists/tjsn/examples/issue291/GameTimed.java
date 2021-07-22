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

import java.util.concurrent.atomic.*;

public class GameTimed {
    private static final LongAccumulator best =
            new LongAccumulator(Long::min, Long.MAX_VALUE);

    // DiceFair 5198ms
    // DiceFast 4300ms
    // DiceVeryFast 3906ms

    public static void main(String... args) {
        for (int i = 0; i < 10; i++) {
            long time = System.nanoTime();
            try {
                Game.main(args);
            } finally {
                time = System.nanoTime() - time;
                best.accumulate(time);
                System.out.printf("time = %dms%n", (time / 1_000_000));
                System.gc();
            }
        }
        System.out.printf("best time = %dms%n",
                (best.longValue() / 1_000_000));
    }
}