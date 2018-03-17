package hadamard;

/**
 * A algorithm state listener for tests.
 */
public class TestAlgorithmStateChangedListener implements IAlgorithmStateChangedListener {
    /**
     * The algorithm state.
     */
    private String state;

    /**
     * Gets the algorithm state.
     *
     * @return The algorithm state.
     */
    public String getState() {
        return state;
    }

    /**
     * Called when the state is changed.
     *
     * @param newState The new state.
     */
    public void stateChanged(String newState) {
        this.state = newState;
    }
}