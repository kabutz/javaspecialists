package eu.javaspecialists.tjsn.concurrency.stripedexecutor;

/**
 * Used to indicate which "stripe" this Runnable or Callable
 * belongs to.
 */
public interface StripedObject {
  Object getStripe();
}
