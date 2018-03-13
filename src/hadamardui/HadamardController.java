package hadamardui;

import hadamard.Configuration;
import hadamard.ThreadDataAggregator;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class HadamardController  implements Initializable {

    private Thread solverThread;
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

    public final EventHandler<WindowEvent> windowIsClosedEventHandler;


    @FXML
    void startButtonClicked(ActionEvent event) {
        if (this.dataAggregator == null)
            this.dataAggregator = new ThreadDataAggregator();
        else this.dataAggregator.reset();

        this.tabPane.getTabs().clear();
        StaticThreadExecutorHelper.setAggregator(this.dataAggregator);
        StaticThreadExecutorHelper.setModel(this.model);

        // We do not need to check for invalid values. If the value would be invalid, the button would be disabled.
        int dimension = Integer.parseInt(model.dimensionProperty().get());

        String choice = strategyChoiceBox.getValue().toString();
        if (choice.equalsIgnoreCase("backtracking") && dimension > 32){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Hadamard");
            alert.setHeaderText("Attention");
            alert.setContentText("Initial rendering for this dimension can take some time!");
            alert.showAndWait().ifPresent(rs -> {});
        }
        if (choice.equalsIgnoreCase("sylvester") && dimension > 32){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Hadamard");
            alert.setHeaderText("Attention");
            alert.setContentText("Rendering for greater dimensions can take some time!");
            alert.showAndWait().ifPresent(rs -> {});
        }


        this.solverThread = new Thread(StaticThreadExecutorHelper::execute);
        solverThread.start();
    }

    @FXML
    void cancelButtonClicked(ActionEvent event) {
        if (this.dataAggregator == null)
            this.dataAggregator.abortAllThreads.set(true);

        this.stopSolverThread();


        this.model.getTabs().clear();
        this.tabPane.getTabs().clear();
    }

    private void stopSolverThread() {
        if (this.solverThread != null) {
            if (this.solverThread.isAlive())
                this.solverThread.interrupt();
        }
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


        UIConfiguration.tabPaneWidthProperty = this.tabPane.widthProperty();
        UIConfiguration.tabPaneHeightProperty = this.tabPane.heightProperty();

    }
    // TODO: Do we need the other cases? 
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
        this.windowIsClosedEventHandler = event -> this.close();
    }

    private void close() {
        this.stopSolverThread();
        System.exit(0);
    }

}
