package hadamardui;

/**
 * Interface for listeners, which are interested in the event of a found result.
 */
public interface IFoundResultListener {
    /**
     * Called when a result has been found.
     *
     * @param threadName The name of the thread, which found the result.
     */
    void resultFound(String threadName);
}
