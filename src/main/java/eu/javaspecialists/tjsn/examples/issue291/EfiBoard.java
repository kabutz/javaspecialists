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

public class EfiBoard extends Board {
    public EfiBoard() {
        super(
                Map.of(
                        17, 6,
                        48, 25,
                        51, 30,
                        59, 40,
                        64, 18,
                        79, 62,
                        83, 75,
                        89, 68,
                        94, 88),
                Map.of(
                        9, 31,
                        19, 38,
                        21, 42,
                        28, 84,
                        36, 57,
                        52, 67,
                        70, 91,
                        80, 99
                ));
    }
}