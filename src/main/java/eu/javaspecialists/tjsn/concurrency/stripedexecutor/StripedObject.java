package eu.javaspecialists.tjsn.concurrency.stripedexecutor;

/**
 * Used to indicate which "stripe" this Runnable or Callable
 * belongs to.  The stripe is determined by the identity of the
 * object, rather than its hash code and equals.
 */
public interface StripedObject {
    Object getStripe();
}
