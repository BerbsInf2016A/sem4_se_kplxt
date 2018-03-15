package hadamardui;

import hadamard.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class HadamardModel implements IMatrixChangedListener, IAlgorithmStateChangedListener {

    private final SimpleBooleanProperty canExecute = new SimpleBooleanProperty(this, "canExecute");
    private final SimpleStringProperty dimension = new SimpleStringProperty(this, "dimension");
    private final SimpleStringProperty applicationState = new SimpleStringProperty(this, "algorithmState");

    private List<Tab> list = new ArrayList<Tab>();
    private ObservableList<Tab> tabs = FXCollections.observableList(list);
    private Strategy strategy;
    private HadamardContext context;
    private List<IFoundResultListener> listeners;

    public HadamardModel() {
        this.dimension.set("2");
        this.strategy = Strategy.Backtracking;
        this.updateContext();
        this.listeners = new ArrayList<>();
    }

    public ObservableList<Tab> getTabs() {
        return tabs;
    }

    public SimpleStringProperty dimensionProperty() {
        return dimension;
    }

    public SimpleBooleanProperty canExecuteProperty() {
        return canExecute;
    }

    public SimpleStringProperty applicationStateProperty() {
        return applicationState;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
        this.updateContext();
    }

    public void addListener(IFoundResultListener listener) {
        if (!this.listeners.contains(listener))
            this.listeners.add(listener);
    }

    public void removeListener(IFoundResultListener listener) {
        if (this.listeners.contains(listener))
            this.listeners.remove(listener);
    }

    public void updateContext() {
        IHadamardStrategy strategy = new BacktrackingAlgorithm();
        switch (this.strategy) {
            case Backtracking:
                strategy = new BacktrackingAlgorithm();
                break;
            case Sylvester:
                strategy = new SylvesterAlgorithm();
                break;
        }
        this.context = new HadamardContext(strategy);

        // There is no try-parse in java....
        try {
            int value = Integer.parseInt(this.dimension.get());
            if (value > 0 && value <= UIConfiguration.MaxDimension) {
                this.canExecute.set(context.canExceuteForDimension(value));
            } else {
                this.canExecute.set(false);
            }

        } catch (NumberFormatException e) {
            this.canExecute.set(false);
        }
    }

    public void execute(ThreadDataAggregator aggregator) {
        if (this.canExecute.get()) {
            // TODO Add Bindings
            aggregator.registerMatrixChangedListener(this);
            aggregator.registerStateChangedListener(this);
            Configuration.instance.dimension = Integer.parseInt(this.dimension.get());
            this.context.executeStrategy(aggregator);
        }
    }

    @Override
    public void matrixChanged(String threadName, Matrix changedMatrix) {
        Platform.runLater(new UpdateUIMatrixTask(threadName, changedMatrix, this.tabs, false));
    }

    @Override
    public void matrixColumnChanged(String threadName, int columnIndex, BitSet column) {
        Platform.runLater(new UpdateUIMatrixColumnTask(threadName, columnIndex, column, this.tabs));
    }

    @Override
    public void resultFound(String threadName, Matrix changedMatrix) {
        Platform.runLater(new UpdateUIMatrixTask(threadName, changedMatrix, this.tabs, true));
        for (IFoundResultListener listener : this.listeners) {
            listener.resultFound(threadName);
        }
    }

    @Override
    public void stateChanged(String newState) {
        Platform.runLater(new UpdateAlgorithmStateTask(this.applicationState, newState));
    }
}
