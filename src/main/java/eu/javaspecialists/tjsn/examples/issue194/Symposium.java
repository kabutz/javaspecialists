/*
 * Copyright (C) 2000-2013 Heinz Max Kabutz
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

package eu.javaspecialists.tjsn.examples.issue194;

import java.util.concurrent.*;

public class Symposium {
    private final Krasi[] cups;
    private final Thinker[] thinkers;

    public Symposium(int delegates) {
        cups = new Krasi[delegates];
        thinkers = new Thinker[delegates];
        for (int i = 0; i < cups.length; i++) {
            cups[i] = new Krasi();
        }
        for (int i = 0; i < delegates; i++) {
            Krasi left = cups[i];
            Krasi right = cups[(i + 1) % delegates];
            thinkers[i] = new Thinker(i, left, right);
        }
    }

    public void run() throws InterruptedException {
        // do this after we created the symposium, so that we do not
        // let the reference to the Symposium escape.
        ExecutorService exec = Executors.newCachedThreadPool();
        CompletionService<String> results =
                new ExecutorCompletionService<>(exec);
        for (Thinker thinker : thinkers) {
            results.submit(thinker);
        }
        System.out.println("Waiting for results");
        for (int i = 0; i < thinkers.length; i++) {
            try {
                System.out.println(results.take().get());
            } catch (ExecutionException e) {
                e.getCause().printStackTrace();
            }
        }
        exec.shutdown();
    }
}