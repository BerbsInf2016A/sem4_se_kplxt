package hadamardui;

import javafx.beans.property.SimpleStringProperty;

/**
 * A task to update the algorithm state in the ui.
 */
public class UpdateAlgorithmStateTask implements Runnable {
    /**
     * The property to set the value to.
     */
    private final SimpleStringProperty algorithmState;
    /**
     * The new state which should be set.
     */
    private final String newState;

    /**
     * Constructor fpr the UpdateAlgorithmStateTask.
     *
     * @param algorithmState The property to work on.
     * @param newState       The new state to set.
     */
    public UpdateAlgorithmStateTask(SimpleStringProperty algorithmState, String newState) {

        this.algorithmState = algorithmState;
        this.newState = newState;
    }

    /**
     * Execute the operation.
     */
    @Override
    public void run() {
        this.algorithmState.set(newState.toString());
    }
}
