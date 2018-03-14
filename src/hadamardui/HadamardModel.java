package hadamardui;

import hadamard.BacktrackingAlgorithm;
import hadamard.Configuration;
import hadamard.HadamardContext;
import hadamard.IHadamardStrategy;
import hadamard.IMatrixChangedListener;
import hadamard.Matrix;
import hadamard.SylvesterAlgorithm;
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
import java.util.Optional;

public class HadamardModel implements IMatrixChangedListener{

    private final SimpleBooleanProperty canExecute = new SimpleBooleanProperty(this, "canExecute");
    private final SimpleStringProperty dimension = new SimpleStringProperty(this, "dimension");

    private List<Tab> list = new ArrayList<Tab>();

    public ObservableList<Tab> getTabs() {
        return tabs;
    }

    private ObservableList<Tab> tabs = FXCollections.observableList(list);

    private Strategy strategy;
    private HadamardContext context;

    private List<IFoundResultListener> listeners;


    public SimpleStringProperty dimensionProperty() {
        return dimension;
    }

    public SimpleBooleanProperty canExecuteProperty() {
        return canExecute;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
        this.updateContext();
    }

    public HadamardModel(){
        this.dimension.set("2");
        this.strategy = Strategy.Backtracking;
        this.updateContext();
        this.listeners = new ArrayList<>();
    }

    public void addListener(IFoundResultListener listener){
        if (!this.listeners.contains(listener))
            this.listeners.add(listener);
    }

    public void removeListener(IFoundResultListener listener){
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
            if (value > 0) {
                this.canExecute.set(context.canExceuteForDimension(value));
            } else{
                this.canExecute.set(false);
            }

        }catch (NumberFormatException e) {
            this.canExecute.set(false);
        }
    }

    public void execute(ThreadDataAggregator aggregator) {
        if (this.canExecute.get()){
            // TODO Add Bindings
            aggregator.registerListener(this);
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
        for (IFoundResultListener listener : this.listeners ) {
            listener.resultFound(threadName);
        }
    }
}
