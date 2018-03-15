package hadamardui;

import hadamard.Configuration;
import hadamard.ThreadDataAggregator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class HadamardController  implements Initializable, IFoundResultListener {

    private Thread solverThread;
    @FXML
    private ChoiceBox strategyChoiceBox;
    @FXML
    private Button startButton;

    @FXML
    private TextField dimension;
    @FXML
    private TabPane tabPane;

    @FXML
    private CheckBox simulateCheckBox;

    @FXML
    private CheckBox cancelAfterFirstResultCheckBox;

    private final SimpleBooleanProperty isRunning = new SimpleBooleanProperty(this, "isRunning");
    private final SimpleBooleanProperty simulateSteps = new SimpleBooleanProperty(this, "simulateSteps");
    private final SimpleBooleanProperty cancelAfterFirstResult = new SimpleBooleanProperty(this, "cancelAfterFirstResult");


    private HadamardModel model;
    private ThreadDataAggregator dataAggregator;

    public final EventHandler<WindowEvent> windowIsClosedEventHandler;


    @FXML
    void startButtonClicked(ActionEvent event) {
        this.isRunning.set(true);
        Configuration.instance.simulateSteps = this.simulateSteps.get();

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
            this.isRunning.set(false);
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
        this.startButton.disableProperty().bind( Bindings.or( model.canExecuteProperty().not(), this.isRunning));
        this.simulateCheckBox.disableProperty().bind(this.isRunning);
        this.simulateCheckBox.selectedProperty().bindBidirectional(this.simulateSteps);

        this.cancelAfterFirstResultCheckBox.disableProperty().bind(this.isRunning);
        this.cancelAfterFirstResultCheckBox.selectedProperty().bindBidirectional(this.cancelAfterFirstResult);
        this.cancelAfterFirstResult.set(Configuration.instance.abortAfterFirstResult);

        this.cancelAfterFirstResult.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    Configuration.instance.abortAfterFirstResult = newValue;
            }});

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
        model.addListener(this);
        this.windowIsClosedEventHandler = event -> this.close();
        this.isRunning.set(false);
    }

    private void close() {
        this.stopSolverThread();
        System.exit(0);
    }

    @Override
    public void resultFound(String threadName) {
        this.isRunning.set(false);
        Optional<Tab> optionalExistingTab = this.tabPane.getTabs().stream().filter(t -> t.getText().equalsIgnoreCase(threadName))
                .findFirst();
        if (optionalExistingTab.isPresent()){
            SingleSelectionModel<Tab> selectionModel = this.tabPane.getSelectionModel();
            Tab tab = optionalExistingTab.get();
            tab.setStyle("-fx-border-color:green; -fx-background-color: green;  -fx-font-weight: bold;");
            selectionModel.select(tab);
        }
    }
}
