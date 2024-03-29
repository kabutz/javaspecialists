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

import java.util.concurrent.*;

public class DiceFast implements Dice {
    private int x = ThreadLocalRandom.current().nextInt();
    private int y = ThreadLocalRandom.current().nextInt();
    private int z = ThreadLocalRandom.current().nextInt();
    private int w = ThreadLocalRandom.current().nextInt();

    // https://www.jstatsoft.org/article/view/v008i14
    public int roll() {
        int tmp = (x ^ (x << 15));
        x = y;
        y = z;
        z = w;
        w = (w ^ (w >> 21)) ^ (tmp ^ (tmp >> 4));
        return mod(w, 6) + 1;
    }

    protected int mod(int x, int div) {
        int result = x % div;
        if (result < 0) return -result;
        return result;
    }
}