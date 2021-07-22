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
import java.util.concurrent.atomic.*;

public class DiceMeasurer {
    private static final int RUNS = 600_000_000;

    private static final LongAccumulator best =
            new LongAccumulator(Long::min, Long.MAX_VALUE);

    public static void main(String... args) {
        for (int i = 0; i < 20; i++) {
            test();
        }
        System.out.printf("best time = %dms%n",
                (best.longValue() / 1_000_000));
    }

    private static void test() {
        long time = System.nanoTime();
        try {
            // Dice dice = new DiceFair(); // 1433 ms
            // Dice dice = new DiceFair(new SecureRandom()); // 85514 ms
            // Dice dice = new DiceFast(); // 1001 ms
            Dice dice = new DiceFastFixedMod(); // 690 ms
            // Dice dice = new DiceVeryFast(); // 372 ms
            int[] results = new int[6];
            for (int i = 0; i < RUNS; i++) {
                results[dice.roll() - 1]++;
            }
            System.out.println("results = " + Arrays.toString(results));
            int[] diffs = new int[6];
            for (int i = 0; i < diffs.length; i++) {
                diffs[i] = results[i] - RUNS / 6;
            }
            System.out.println("diffs = " + Arrays.toString(diffs));
        } finally {
            time = System.nanoTime() - time;
            best.accumulate(time);
            System.out.printf("time = %dms%n", (time / 1_000_000));
        }
    }
}
