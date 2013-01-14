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

package eu.javaspecialists.tjsn.examples.issue199.dream;

import eu.javaspecialists.tjsn.examples.issue199.sleep.*;

/**
 * @author Heinz Kabutz
 */
public class DreamSolution {
    private final Thread constructThread = Thread.currentThread();

    public void dream(final SleeperSolution sleeper) {
        if (Thread.currentThread() == constructThread) {
            System.out.println("Constructor thread trying to dream");
            new Thread("nightmare") {
                public void run() {
                    sleeper.enter(DreamSolution.this);
                }
            }.start();
            try {
                sleeper.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(Thread.currentThread().getName() +
                    " thread trying to dream");
            synchronized (sleeper) {
                sleeper.notify();
                try {
                    sleeper.wait(100);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
}
