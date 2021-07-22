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

import java.lang.invoke.*;
import java.util.concurrent.*;

public class DiceVeryFast implements Dice {
    // fits in L2 cache at 512k
    private static final byte[] rolls = new byte[512 * 1024];

    private int pos = ThreadLocalRandom.current()
            .nextInt(0, rolls.length);
    private static final int MASK = rolls.length - 1;

    public int roll() {
        return (byte) ROLLS.getOpaque(rolls, (pos++ & MASK));
    }

    private static final VarHandle ROLLS =
            MethodHandles.arrayElementVarHandle(byte[].class);

    static {
        fillWithRandom();
        Thread shuffler = new Thread(() -> {
            while (true) {
                fillWithRandom();
            }
        }, "DiceVeryFast-Shuffler");
        shuffler.setDaemon(true);
        shuffler.start();
    }

    private static void fillWithRandom() {
        int size = rolls.length;
        var rnd = ThreadLocalRandom.current();
        for (int i = size - 1; i >= 0; i--) {
            ROLLS.setOpaque(rolls, i, (byte) rnd.nextInt(1, 7));
        }
    }
}
