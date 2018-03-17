package hadamardui;

import hadamard.Configuration;
import hadamard.ThreadDataAggregator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the HadamardUI.
 */
public class HadamardController implements Initializable, IFoundResultListener {

    /**
     * Eventhandler for the window closed event.
     */
    public final EventHandler<WindowEvent> windowIsClosedEventHandler;
    /**
     * Property to bind the ui to. Is true, when the algorithm is running, false if not.
     */
    private final SimpleBooleanProperty isRunning = new SimpleBooleanProperty(this, "isRunning");
    /**
     * Property to bind the ui to. Is true, when the single steps should be visible on the ui, false if not.
     */
    private final SimpleBooleanProperty simulateSteps = new SimpleBooleanProperty(this, "simulateSteps");
    /**
     * Property to bind the ui to. Is true, if the algorithm should be stopped after the first result, false if not.
     */
    private final SimpleBooleanProperty cancelAfterFirstResult = new SimpleBooleanProperty(this, "cancelAfterFirstResult");
    /**
     * The thread which executes the algorithm.
     */
    private Thread solverThread;
    /**
     * ChoiceBox for the strategy.
     */
    @FXML
    private ChoiceBox strategyChoiceBox;
    /**
     * The button to start the algorithm.
     */
    @FXML
    private Button startButton;
    /**
     * Textfield containing the dimension.
     */
    @FXML
    private TextField dimension;
    /**
     * The tabPane containing the different tabs for algorithm.
     */
    @FXML
    private TabPane tabPane;
    /**
     * CheckBox to indicate, if the single steps should be simulated.
     */
    @FXML
    private CheckBox simulateCheckBox;
    /**
     * CheckBox to indicate, if the algorithm should be stopped after the first result.
     */
    @FXML
    private CheckBox cancelAfterFirstResultCheckBox;
    /**
     * Label for the current algorithm state.
     */
    @FXML
    private Label algorithmState;
    /**
     * The model, which contains the algorithm.
     */
    private HadamardModel model;
    /**
     * The data aggregator which should be used to gather the data.
     */
    private ThreadDataAggregator dataAggregator;

    /**
     * Constructor for the HadamardController.
     */
    public HadamardController() {
        this.model = new HadamardModel();
        model.addListener(this);
        this.windowIsClosedEventHandler = event -> this.close();
        this.isRunning.set(false);
    }

    /**
     * Called when the start button is clicked.
     *
     * @param event The event data.
     */
    @FXML
    void startButtonClicked(ActionEvent event) {
        this.isRunning.set(true);
        Configuration.instance.simulateSteps = this.simulateSteps.get();

        if (this.dataAggregator == null)
            this.dataAggregator = new ThreadDataAggregator();
        else this.dataAggregator.reset();

        // Clear previous results.
        this.tabPane.getTabs().clear();
        StaticThreadExecutorHelper.setAggregator(this.dataAggregator);
        StaticThreadExecutorHelper.setModel(this.model);

        // We do not need to check for invalid values. If the value would be invalid, the button would be disabled.
        int dimension = Integer.parseInt(model.dimensionProperty().get());

        // Warn the user if a certain dimension limit is reached, because executing can take some time.
        String choice = strategyChoiceBox.getValue().toString();
        if (choice.equalsIgnoreCase("backtracking") && dimension > 32) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Hadamard");
            alert.setHeaderText("Attention");
            alert.setContentText("Initial rendering for this dimension can take some time!");
            alert.showAndWait().ifPresent(rs -> {
            });
        }
        if (choice.equalsIgnoreCase("sylvester") && dimension > 32) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Hadamard");
            alert.setHeaderText("Attention");
            alert.setContentText("Rendering for greater dimensions can take some time!");
            alert.showAndWait().ifPresent(rs -> {
            });
        }

        // Start execution.
        this.solverThread = new Thread(StaticThreadExecutorHelper::execute);
        solverThread.start();
    }

    /**
     * Called when the cancel button is clicked.
     */
    @FXML
    void cancelButtonClicked() {
        // Abort execution.
        if (this.dataAggregator == null)
            this.dataAggregator.abortAllThreads.set(true);

        this.stopSolverThread();

        // Remove current tabs.
        this.model.getTabs().clear();
        this.tabPane.getTabs().clear();
        this.model.algorithmStateProperty().set("Waiting");
    }

    /**
     * Stop the solver thread.
     */
    private void stopSolverThread() {
        if (this.solverThread != null) {
            if (this.solverThread.isAlive())
                this.solverThread.interrupt();
            this.isRunning.set(false);
        }
    }

    /**
     * Called when the selected strategy is changed.
     */
    @FXML
    void choiceMade() {
        String choice = strategyChoiceBox.getValue().toString();
        if (choice.equalsIgnoreCase("sylvester"))
            this.model.setStrategy(Strategy.Sylvester);
        if (choice.equalsIgnoreCase("backtracking"))
            this.model.setStrategy(Strategy.Backtracking);
    }

    /**
     * Initialize the ui elements.
     *
     * @param url            The url.
     * @param resourceBundle The resource bundle.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        this.model.setStrategy(Strategy.Sylvester);

        // Add the bindings and listeners:
        this.dimension.textProperty().bindBidirectional(model.dimensionProperty());
        this.dimension.textProperty().addListener((observable, oldValue, newValue) -> model.updateContext());

        this.startButton.disableProperty().bind(Bindings.or(model.canExecuteProperty().not(), this.isRunning));

        this.simulateCheckBox.disableProperty().bind(this.isRunning);
        this.simulateCheckBox.selectedProperty().bindBidirectional(this.simulateSteps);

        this.cancelAfterFirstResultCheckBox.disableProperty().bind(this.isRunning);
        this.cancelAfterFirstResultCheckBox.selectedProperty().bindBidirectional(this.cancelAfterFirstResult);
        this.cancelAfterFirstResult.set(Configuration.instance.abortAfterFirstResult);
        this.cancelAfterFirstResult.addListener((observable, oldValue, newValue)
                -> Configuration.instance.abortAfterFirstResult = newValue);

        model.getTabs().addListener((ListChangeListener<Tab>) change -> {
            if (change.next()) {
                handleChangeModelTab(change, tabPane);
            }
        });

        this.algorithmState.textProperty().bind(model.algorithmStateProperty());
        this.model.algorithmStateProperty().set("Waiting");

        UIConfiguration.tabPaneWidthProperty = this.tabPane.widthProperty();
        UIConfiguration.tabPaneHeightProperty = this.tabPane.heightProperty();
    }

    /**
     * Handles adding and removing of tabs.
     *
     * @param change  The change, which occurred.
     * @param tabPane The tabPane to act on.
     */
    private void handleChangeModelTab(ListChangeListener.Change<? extends Tab> change, TabPane tabPane) {
        for (Tab removeTab : change.getRemoved())
            tabPane.getTabs().remove(removeTab);
        for (Tab addTab : change.getAddedSubList())
            tabPane.getTabs().add(addTab);
    }

    /**
     * Will close the program.
     */
    private void close() {
        this.stopSolverThread();
        System.exit(0);
    }

    /**
     * Selects the tab, which is identified by the threadName. Will highlight it as a result.
     *
     * @param threadName The name of the thread, which found the result.
     */
    @Override
    public void resultFound(String threadName) {
        this.isRunning.set(false);
        Optional<Tab> optionalExistingTab = this.tabPane.getTabs().stream().filter(t -> t.getText().equalsIgnoreCase(threadName))
                .findFirst();
        optionalExistingTab.ifPresent(tab -> {
            SingleSelectionModel<Tab> selectionModel = this.tabPane.getSelectionModel();
            tab.setStyle(UIConfiguration.instance.resultTabStyle);
            selectionModel.select(tab);
        });
    }
}
