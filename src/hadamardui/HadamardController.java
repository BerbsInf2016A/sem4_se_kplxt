package hadamardui;

import hadamard.ThreadDataAggregator;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class HadamardController  implements Initializable {
    @FXML
    private ChoiceBox strategyChoiceBox;
    @FXML
    private Button startButton;

    @FXML
    private TextField dimension;
    @FXML
    private TabPane tabPane;


    private HadamardModel model;
    private ThreadDataAggregator dataAggregator;


    @FXML
    void startButtonClicked(ActionEvent event) {
        if (this.dataAggregator == null)
            this.dataAggregator = new ThreadDataAggregator();
        else this.dataAggregator.reset();
        this.model.execute(this.dataAggregator);
    }

    @FXML
    void choiceMade(ActionEvent event) {
        String choice = strategyChoiceBox.getValue().toString();
        if (choice.equalsIgnoreCase("sylvester")){
            this.model.setStrategy(Strategy.Sylvester);
        }
        if (choice.equalsIgnoreCase("backtracking")){
            this.model.setStrategy(Strategy.Backtracking);
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       // Add Bindings
        int g = 5;

        this.model.setStrategy(Strategy.Sylvester);
        this.dimension.textProperty().bindBidirectional(model.dimensionProperty());
        this.dimension.textProperty().addListener((observable, oldValue, newValue) -> {
            model.updateContext();
        });
        this.startButton.disableProperty().bind(model.canExecuteProperty().not());



        model.getTabs().addListener(new ListChangeListener<Tab>() {

            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<? extends Tab> change) {

                if(change.next()){
                    handleChangeModelTab(change, tabPane);
                }
            }
        });



    }
    private void handleChangeModelTab(ListChangeListener.Change<? extends Tab> change, TabPane tabPane) {
        if (change.wasUpdated()) {
            int g = 2;
        } else if (change.wasPermutated()) {
            int g = 2;
        }
        else {
            for (Tab remitem : change.getRemoved()) {
                tabPane.getTabs().remove(remitem);
            }
            for (Tab additem : change.getAddedSubList()) {
                tabPane.getTabs().add(additem);
            }
        }
    }

    public HadamardController(){
        this.model = new HadamardModel();
    }

}
