package eu.javaspecialists.tjsn.concurrency.stripedexecutor;

import java.util.concurrent.*;

public interface StripedCallable<V> extends Callable<V>, StripedObject {
}