package eu.javaspecialists.tjsn.concurrency.interlocker;


import eu.javaspecialists.tjsn.concurrency.interlocker.impl.*;

/**
 * Helper class for creating lock free vs. blocking interlockers.
 * <p/>
 * Described in http://www.javaspecialists.eu/archive/Issue188.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class Interlockers {
    public static Interlocker createLockFreeInterlocker() {
        return new LockFreeInterlocker();
    }

    public static Interlocker createBlockingInterlocker() {
        return new SemaphoreInterlocker();
    }
}