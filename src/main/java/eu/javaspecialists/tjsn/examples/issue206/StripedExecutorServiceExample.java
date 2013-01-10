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

package eu.javaspecialists.tjsn.examples.issue206;

import eu.javaspecialists.tjsn.concurrency.stripedexecutor.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * Submits some jobs to a StripedExecutorService and prints out
 * the order of execution.
 * <p/>
 * See http://www.javaspecialists.eu/archive/Issue206.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class StripedExecutorServiceExample {
    private static final int UPTO = 10;

    public static void main(String[] args)
            throws InterruptedException {
        test(new StripedExecutorService());
        test(Executors.newCachedThreadPool());
    }

    private static void test(ExecutorService pool)
            throws InterruptedException {
        final Vector<Integer> call_sequence = new Vector<>();
        for (int i = 0; i < UPTO; i++) {
            final int tempI = i;
            pool.submit(new StripedCallable<Void>() {
                public Void call() throws Exception {
                    TimeUnit.MILLISECONDS.sleep(
                            ThreadLocalRandom.current().nextInt(2, 10)
                    );
                    call_sequence.add(tempI);
                    return null;
                }

                public Object getStripe() {
                    return call_sequence;
                }
            });
        }
        pool.shutdown();
        while (!pool.awaitTermination(1, TimeUnit.SECONDS)) ;
        System.out.println(call_sequence);
    }
}
