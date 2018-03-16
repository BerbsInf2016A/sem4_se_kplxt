package hadamard;

/**
 * Interface for listeners, which are interested in the event of changes of the algorithm state.
 */
public interface IAlgorithmStateChangedListener {
    /**
     * Called, when the algorithm state has changed.
     *
     * @param newState The new state.
     */
    void stateChanged(String newState);
}
