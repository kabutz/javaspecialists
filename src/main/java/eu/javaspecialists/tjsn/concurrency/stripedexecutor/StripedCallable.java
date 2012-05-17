package eu.javaspecialists.tjsn.concurrency.stripedexecutor;

import java.util.concurrent.*;

/**
 * All of the Callables in the same "Stripe" will be executed consecutively.
 *
 * @author Dr Heinz M. Kabutz
 */
public interface StripedCallable<V> extends Callable<V>, StripedObject {
}