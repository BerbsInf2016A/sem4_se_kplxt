package hadamardui;

import javafx.beans.property.SimpleStringProperty;

public class UpdateAlgorithmStateTask implements Runnable {
    private final SimpleStringProperty applicationState;
    private final String newState;

    public UpdateAlgorithmStateTask(SimpleStringProperty applicationState, String newState) {

        this.applicationState = applicationState;
        this.newState = newState;
    }

    @Override
    public void run() {
        this.applicationState.set(newState.toString());
    }
}
