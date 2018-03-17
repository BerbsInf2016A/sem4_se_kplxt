package hadamardui;

import hadamard.BacktrackingAlgorithmStrategy;
import hadamard.Configuration;
import hadamard.HadamardContext;
import hadamard.IAlgorithmStateChangedListener;
import hadamard.IHadamardStrategy;
import hadamard.IMatrixChangedListener;
import hadamard.Matrix;
import hadamard.SylvesterAlgorithmStrategy;
import hadamard.ThreadDataAggregator;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * A model for the hadamard ui to bind the algorithm to the ui.
 */
public class HadamardModel implements IMatrixChangedListener, IAlgorithmStateChangedListener {

    /**
     * A property to bind the canExecute flag from the algorithm to the ui.
     */
    private final SimpleBooleanProperty canExecute = new SimpleBooleanProperty(this, "canExecute");
    /**
     * A property to bind the dimension from the ui to the model.
     */
    private final SimpleStringProperty dimension = new SimpleStringProperty(this, "dimension");
    /**
     * A property to bind the state of the algorithm to the ui.
     */
    private final SimpleStringProperty algorithmState = new SimpleStringProperty(this, "algorithmState");

    /**
     * The tabs to show in the ui. Each search thread has its own tab.
     */
    private List<Tab> list = new ArrayList<>();
    /**
     * The tabs to show in the ui. Each search thread has its own tab.
     */
    private ObservableList<Tab> tabs = FXCollections.observableList(list);
    /**
     * The selected strategy.
     */
    private Strategy strategy;
    /**
     * The context of the algorithm.
     */
    private HadamardContext context;
    /**
     * List of listeners, which are interested in the event of a found result.
     */
    private List<IFoundResultListener> listeners;

    /**
     * Constructor for the HadamardModel.
     */
    public HadamardModel() {
        this.dimension.set("2");
        this.strategy = Strategy.Backtracking;
        this.updateContext();
        this.listeners = new ArrayList<>();
    }

    /**
     * Get the tabs of the model.
     *
     * @return A observable list of tabs.
     */
    public ObservableList<Tab> getTabs() {
        return tabs;
    }

    /**
     * Get the dimension property.
     *
     * @return The dimension property.
     */
    public SimpleStringProperty dimensionProperty() {
        return dimension;
    }

    /**
     * Get the canExecute property.
     *
     * @return The canExecute property.
     */
    public SimpleBooleanProperty canExecuteProperty() {
        return canExecute;
    }

    /**
     * Get the algorithmState property.
     *
     * @return The algorithmState property.
     */
    public SimpleStringProperty algorithmStateProperty() {
        return algorithmState;
    }

    /**
     * Set the strategy for the algorithm.
     *
     * @param strategy The strategy for the algorithm.
     */
    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
        this.updateContext();
    }

    /**
     * Add a listener for the event of a found result.
     *
     * @param listener The listener to add.
     */
    public void addListener(IFoundResultListener listener) {
        if (!this.listeners.contains(listener))
            this.listeners.add(listener);
    }

    /**
     * Remove a listener.
     *
     * @param listener The listener to remove.
     */
    public void removeListener(IFoundResultListener listener) {
        if (this.listeners.contains(listener))
            this.listeners.remove(listener);
    }

    /**
     * Update the context of the algorithm
     */
    public void updateContext() {
        // Set the strategy.
        IHadamardStrategy strategy = new BacktrackingAlgorithmStrategy();
        switch (this.strategy) {
            case Backtracking:
                strategy = new BacktrackingAlgorithmStrategy();
                break;
            case Sylvester:
                strategy = new SylvesterAlgorithmStrategy();
                break;
        }
        this.context = new HadamardContext(strategy);
        // Set the canExecute value.
        // There is no try-parse in java....
        try {
            int value = Integer.parseInt(this.dimension.get());
            if (value > 0 && value <= UIConfiguration.maxDimension) {
                this.canExecute.set(context.canExecuteForDimension(value));
            } else {
                this.canExecute.set(false);
            }

        } catch (NumberFormatException e) {
            this.canExecute.set(false);
        }
    }

    /**
     * Execute the algorithm.
     *
     * @param aggregator The ThreadAggregator to use.
     */
    public void execute(ThreadDataAggregator aggregator) {
        if (this.canExecute.get()) {
            aggregator.registerMatrixChangedListener(this);
            aggregator.registerStateChangedListener(this);
            // There is no need to check for parse failures. The start button in the ui is blocked, if execution is not
            // possible.
            Configuration.instance.dimension = Integer.parseInt(this.dimension.get());
            this.context.executeStrategy(aggregator);
        }
    }

    /**
     * Called when a matrix was changed. Updates the complete matrix on the ui.
     *
     * @param threadName    The name of the thread which wants to update its matrix.
     * @param changedMatrix The matrix value to set.
     */
    @Override
    public void matrixChanged(String threadName, Matrix changedMatrix) {
        // Execute on the ui thread.
        Platform.runLater(new UpdateUIMatrixTask(threadName, changedMatrix, this.tabs, false));
    }

    /**
     * Called when a single column of a matrix should be updated.
     *
     * @param threadName  The name of the thread which wants to update its matrix.
     * @param columnIndex Index of the column to update.
     * @param column      The column value to set.
     */
    @Override
    public void matrixColumnChanged(String threadName, int columnIndex, BitSet column) {
        // Execute on the ui thread.
        Platform.runLater(new UpdateUIMatrixColumnTask(threadName, columnIndex, column, this.tabs));
    }

    /**
     * Called when a result is found.
     *
     * @param threadName   The name of the thread which wants to update its matrix.
     * @param resultMatrix The matrix value to set.
     */
    @Override
    public void resultFound(String threadName, Matrix resultMatrix) {
        // Execute on the ui thread.
        Platform.runLater(new UpdateUIMatrixTask(threadName, resultMatrix, this.tabs, true));
        for (IFoundResultListener listener : this.listeners) {
            listener.resultFound(threadName);
        }
    }

    /**
     * Called when the algorithm state changed.
     *
     * @param newState The new state to set.
     */
    @Override
    public void stateChanged(String newState) {
        // Execute on the ui thread.
        Platform.runLater(new UpdateAlgorithmStateTask(this.algorithmState, newState));
    }
}
