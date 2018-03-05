package hadamardui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

public class HadamardController {
    @FXML
    private ChoiceBox strategyChoiceBox;

    @FXML
    void choiceMade(ActionEvent event) {
        String choice = strategyChoiceBox.getValue().toString();

    }

}
