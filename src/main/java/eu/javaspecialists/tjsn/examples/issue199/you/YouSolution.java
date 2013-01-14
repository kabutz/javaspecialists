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

package eu.javaspecialists.tjsn.examples.issue199.you;

import eu.javaspecialists.tjsn.examples.issue199.clowns.*;

import java.util.concurrent.*;

/**
 * @author Heinz Kabutz
 */
public class YouSolution {
    private static final int DESIRED_CLOWNS = 20;

    public static void main(String args[]) throws InterruptedException {
        solve();
    }

    public static boolean solve() throws InterruptedException {
        final Volkswagen vw = new Volkswagen();
        final CountDownLatch latch = new CountDownLatch(DESIRED_CLOWNS);
        Thread[] threads = new Thread[DESIRED_CLOWNS];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread() {
                public void run() {
                    vw.add(new ClownSolution(vw, latch));
                }
            };
            threads[i].start();
        }
        latch.await();
        synchronized (vw) {
            vw.notifyAll();
        }
        for (Thread thread : threads) {
            thread.join(100);
            if (thread.isAlive()) return false;
        }
        return vw.done();
    }

    public static class ClownSolution extends Clown {
        // we need hasWaited as hashCode is called twice in some cases
        private boolean hasWaited = false;
        private final Volkswagen volksie;
        private final CountDownLatch latch;

        public ClownSolution(Volkswagen volksie, CountDownLatch latch) {
            this.volksie = volksie;
            this.latch = latch;
        }

        public int hashCode() {
            if (!hasWaited) {
                synchronized (volksie) {
                    latch.countDown();
                    try {
                        volksie.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    hasWaited = true;
                }
            }
            return super.hashCode();
        }
    }
}