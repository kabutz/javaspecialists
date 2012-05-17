package eu.javaspecialists.tjsn.concurrency.interlocker;

/**
 * Used by the InterleavedNumberTestingStrategy to return either success or
 * failure from each run.
 * <p/>
 * Described in http://www.javaspecialists.eu/archive/Issue188.html
 *
 * @author Dr Heinz M. Kabutz
 */
public class VerifyResult {
    private final boolean success;
    private final String failReason;

    private VerifyResult(boolean success, String failReason) {
        this.success = success;
        this.failReason = failReason;
    }

    public VerifyResult(String failReason) {
        this(false, failReason);
    }

    public VerifyResult() {
        this(true, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getFailReason() {
        return failReason;
    }

    public String toString() {
        return success ? "Success" : "Failure - " + failReason;
    }
}