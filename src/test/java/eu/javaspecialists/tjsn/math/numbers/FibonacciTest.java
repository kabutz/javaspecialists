/*
 * Copyright (C) 2000-2012 Heinz Max Kabutz
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

package eu.javaspecialists.tjsn.math.numbers;

import eu.javaspecialists.tjsn.math.fibonacci.*;
import org.junit.*;

import java.math.*;
import java.util.concurrent.*;

import static junit.framework.Assert.*;

/**
 * We test the fibonacci functions first for correctness in the lower numbers
 * and then also for performance.  Due to the great variance in results on
 * different hardware, our performance tests do not fail if a run is
 * particularly slow, but it does cancel calculations if they take too long.
 *
 * @author Dr Heinz M. Kabutz
 */
public class FibonacciTest {
    private final static ForkJoinPool pool = new ForkJoinPool();

    private final static Fibonacci[] FIBONACCIS = {
            new FibonacciFormulaLong(),
            new FibonacciRecursiveNonCaching(),
            new FibonacciIterative(),
            new FibonacciRecursive(),
            new FibonacciRecursiveDijkstra(),
            new FibonacciRecursiveDijkstraKaratsuba(),
            new FibonacciRecursiveParallelDijkstraKaratsuba(pool),
            new FibonacciFormulaBigInteger(),
            new FibonacciTakahashi(),
    };

    @Test
    public void testSmallCorrectness() throws InterruptedException {
        for (Fibonacci fibonacci : FIBONACCIS) {
            checkSmallCorrectness(fibonacci);
        }
    }

    private void checkSmallCorrectness(Fibonacci fibonacci) throws InterruptedException {
        int n = 0;
        assertEquals(BigInteger.ZERO, fibonacci.calculate(n++));
        assertEquals(BigInteger.ONE, fibonacci.calculate(n++));
        assertEquals(BigInteger.ONE, fibonacci.calculate(n++));
        assertEquals(new BigInteger("2"), fibonacci.calculate(n++));
        assertEquals(new BigInteger("3"), fibonacci.calculate(n++));
        assertEquals(new BigInteger("5"), fibonacci.calculate(n++));
        assertEquals(new BigInteger("8"), fibonacci.calculate(n++));
        assertEquals(new BigInteger("13"), fibonacci.calculate(n++));
        assertEquals(new BigInteger("21"), fibonacci.calculate(n++));
        assertEquals(new BigInteger("34"), fibonacci.calculate(n++));
        assertEquals(new BigInteger("55"), fibonacci.calculate(n++));
        assertEquals(new BigInteger("89"), fibonacci.calculate(n++));
        assertEquals(new BigInteger("144"), fibonacci.calculate(n++));
    }

    @Test
    public void testLargeCorrectness() throws InterruptedException {
        for (Fibonacci fibonacci : FIBONACCIS) {
            try {
                checkLargeCorrectness(fibonacci, 1, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                System.out.println(fibonacci.getClass().getSimpleName() +
                        " timed out on large calculation " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println(fibonacci.getClass().getSimpleName() +
                        " issue with calculation: " + e.getMessage());
            } catch (StackOverflowError e) {
                System.out.println(fibonacci.getClass().getSimpleName() +
                        " issue with calculation - StackOverflowError");
            }
        }
    }

    private void checkLargeCorrectness(Fibonacci fibonacci,
                                       int timeout, TimeUnit unit)
            throws TimeoutException, InterruptedException {
        BigInteger result;
        result = executeAsynchronously(fibonacci, 71, timeout, unit);
        assertEquals(new BigInteger("308061521170129"), result);
        result = executeAsynchronously(fibonacci, 72, timeout, unit);
        assertEquals(new BigInteger("498454011879264"), result);
        result = executeAsynchronously(fibonacci, 400, timeout, unit);
        assertEquals(new BigInteger("176023680645013966468226945392411250770" +
                "384383304492191886725992896575345044216019675"), result);
        result = executeAsynchronously(fibonacci, 700, timeout, unit);
        assertEquals(new BigInteger("874708149557528462039784130175713273423" +
                "67240967697381074230432592527501911290377655628227150878427" +
                "331693193369109193672330777527943718169105124275"), result);
        result = executeAsynchronously(fibonacci, 1000, timeout, unit);
        assertEquals(new BigInteger("434665576869374564356885276750406258025" +
                "64660517371780402481729089536555417949051890403879840079255" +
                "16929592259308032263477520968962323987332247116164299644090" +
                "6533187938298969649928516003704476137795166849228875"), result);
        result = executeAsynchronously(fibonacci, 10_000, timeout, unit);
        assertEquals(new BigInteger("336447648764317832666216120051075433103" +
                "02148460680063906564769974680081442166662368155595513633734" +
                "02558206533268083615937373479048386526826304089246305643188" +
                "73545443695598274916066020998841839338646527313000888302692" +
                "35673613135117579297437854413752130520504347701602264758318" +
                "90652789085515436615958298727968298751063120057542878345321" +
                "55151038708182989697916131278562650331954871402142875326981" +
                "87962046936097879900350962302291026368131493195275630227837" +
                "62844154036058440257211433496118002309120828704608892396232" +
                "88354615057765832712525460935911282039252853934346209042452" +
                "48929403901706233888991085841065183173360437470737908552631" +
                "76432573399371287193758774689747992630583706574283016163740" +
                "89691784263786242128352581128205163702980893320999057079200" +
                "64367426202389783111470054074998459250360633560933883831923" +
                "38678305613643535189213327973290813373264265263398976392272" +
                "34078829281779535805709936910491754708089318410561463223382" +
                "17465637321248226383092103297701648054726243842374862411453" +
                "09381220656491403275108664339451751216152654536133311131404" +
                "24368548051067658434935238369596534280717687753283482343455" +
                "57366719731392746273629108210679280784718035329131176778924" +
                "65908993863545932789452377767440619224033763867400402133034" +
                "32974969020283281459334188268176838930720036347956231171031" +
                "01291953169794607632737589253530772552375943788434504067715" +
                "55577905645044301664011946258097221672975861502696844314695" +
                "20346149322911059706762432685159928347098912847067408620085" +
                "87135016260312071903172086094081298321581077282076353186624" +
                "61127824553720853236530577595643007251774431505153960090516" +
                "86032203491632226408852488524331580515348496224348482993809" +
                "05070483482449327453732624567755879089187190803662058009594" +
                "74315005240253270974699531877072437682590741993963226598414" +
                "74981936092852239450397071654431564213281576889080587831834" +
                "04917434556270520223564846495196112460268313970975069382648" +
                "70661326450766507461151267752274862159864253071129844118262" +
                "26610571635150692600298617049454250474913781151541399415506" +
                "71256271197133252763631939606902895650288268608362241082050" +
                "562430701794976171121233066073310059947366875"), result);

        result = executeAsynchronously(fibonacci, 1_000_000, timeout, unit);
        assertEquals(694241, result.bitLength());

        /*
        turns out that add() is faster than shift or multiply
        BigInteger TWO = new BigInteger("2");

        Karatsuba bkar = new BasicKaratsuba();
        ForkJoinPool pool = new ForkJoinPool();
        Karatsuba pkar = new ParallelKaratsuba(pool);

        assertEquals(result.shiftLeft(1), result.multiply(TWO));
        assertEquals(result.shiftLeft(1), result.add(result));
        assertEquals(result.shiftLeft(1), bkar.multiply(result, TWO));
        assertEquals(result.shiftLeft(1), pkar.multiply(result, TWO));

        for (int j = 0; j < 10; j++) {
            long time;
            time = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
                result.multiply(TWO);
            }
            time = System.currentTimeMillis() - time;
            System.out.println("Multiply by TWO = " + time);

            time = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
                result.shiftLeft(1);
            }
            time = System.currentTimeMillis() - time;
            System.out.println("Left shift = " + time);

            time = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
                result.add(result);
            }
            time = System.currentTimeMillis() - time;
            System.out.println("add = " + time);

            time = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
                bkar.multiply(result, TWO);
            }
            time = System.currentTimeMillis() - time;
            System.out.println("BasicKaratsuba = " + time);

            time = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
                pkar.multiply(result, TWO);
            }
            time = System.currentTimeMillis() - time;
            System.out.println("ParallelKaratsuba = " + time);
        }
        pool.shutdown();
        //*/

    }

    private BigInteger executeAsynchronously(final Fibonacci fibonacci,
                                             final int n,
                                             long timeout, TimeUnit unit)
            throws TimeoutException, InterruptedException {
        long time = System.currentTimeMillis();
        ExecutorService pool = Executors.newSingleThreadExecutor();
        Future<BigInteger> result = pool.submit(new Callable<BigInteger>() {
            @Override
            public BigInteger call() throws Exception {
                return fibonacci.calculate(n);
            }
        });
        pool.shutdown();
        try {
            BigInteger answer = result.get(timeout, unit);
            time = System.currentTimeMillis() - time;
            System.out.println(fibonacci.getClass().getSimpleName() +
                    " calculated fib(" + n + ") in " + time + "ms");
            return answer;
        } catch (InterruptedException e) {
            throw e;
        } catch (TimeoutException e) {
            throw new TimeoutException("n=" + n);
        } catch (ExecutionException e) {
            try {
                throw e.getCause();
            } catch (RuntimeException re) {
                throw re;
            } catch (Error er) {
                throw er;
            } catch (Throwable throwable) {
                throw new IllegalStateException(throwable);
            }
        } finally {
            pool.shutdownNow();
            try {
                assertTrue(pool.awaitTermination(1, TimeUnit.MINUTES));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Test
    public void testParallelExecution() throws InterruptedException {
        ForkJoinPool pool = new ForkJoinPool(128);
        Fibonacci fib = new FibonacciRecursiveParallelDijkstraKaratsuba(pool);
        long time = System.currentTimeMillis();
        fib.calculate(2_000_000);
        time = System.currentTimeMillis() - time;
        System.out.println("Solved fib(10_000_000) in " + time + "ms parallel");
        System.out.println(pool);
        assertEquals(128, pool.getParallelism());
        pool.shutdown();
    }
}
