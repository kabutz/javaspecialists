package eu.javaspecialists.tjsn.examples.issue201;

import eu.javaspecialists.tjsn.math.fibonacci.*;

import java.util.concurrent.*;

public class FibonacciGeneratorExample {
    private static ForkJoinPool pool = new ForkJoinPool(
            Runtime.getRuntime().availableProcessors() * 4);

    public static void main(String[] args) throws InterruptedException {
        int[] ns;
        if (args.length != 0) {
            ns = new int[args.length];
            for (int i = 0; i < ns.length; i++) {
                ns[i] = Integer.parseInt(args[i]);
            }
        } else {
            ns = new int[]{
                    1_000_000,
                    10_000_000,
                    100_000_000, // takes a bit long
                    1000_000_000, // takes a bit long
            };
        }
        test(new FibonacciRecursiveParallelDijkstraKaratsuba(pool), ns);
    }

    private static void test(Fibonacci fib, int... ns) throws InterruptedException {
        for (int n : ns) {
            FibonacciGenerator fibgen = new FibonacciGenerator(fib);
            fibgen.findFib(n);
            System.out.println(pool);
        }
    }
}