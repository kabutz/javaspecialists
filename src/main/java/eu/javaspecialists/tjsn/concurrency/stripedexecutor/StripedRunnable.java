package eu.javaspecialists.tjsn.concurrency.stripedexecutor;

/**
 * All of the Runnables in the same "Stripe" will be executed
 * consecutively.
 *
 * @author Dr Heinz M. Kabutz
 */
public interface StripedRunnable extends Runnable, StripedObject {
}