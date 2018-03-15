package hadamard;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadDataAggregator {

    public static AtomicBoolean resultFound = new AtomicBoolean();
    public static AtomicBoolean abortAllThreads = new AtomicBoolean();
    public static List<String> threadsWithResults = Collections.synchronizedList(new ArrayList());

    private List<IMatrixChangedListener> matrixChangedListeners;
    private List<IAlgorithmStateChangedListener> algorithmStateListeners;

    public ThreadDataAggregator() {
        this.matrixChangedListeners = new ArrayList<>();
        this.algorithmStateListeners = new ArrayList<>();
    }

    public void updateMatrix(String threadName, Matrix matrix) {
        if (Configuration.instance.printDebugMessages)
            System.out.println(matrix.getUIDebugStringRepresentation());
        for (IMatrixChangedListener listener : this.matrixChangedListeners) {
            listener.matrixChanged(threadName, matrix);
        }
    }

    public void updateMatrixColumn(String threadName, int columnIndex, BitSet column) {
        for (IMatrixChangedListener listener : this.matrixChangedListeners) {
            listener.matrixColumnChanged(threadName, columnIndex, column);
        }
    }

    public void setResult(String threadName, Matrix matrix) {
        if (Configuration.instance.printDebugMessages) {
            System.out.println(threadName + " setting result");
        }
        resultFound.set(true);

        if (!threadsWithResults.contains(threadName))
            threadsWithResults.add(threadName);

        this.notifyResultFound(threadName, matrix);
        if (Configuration.instance.abortAfterFirstResult) {
            abortAllThreads.set(true);
        }

        this.setApplicationState(AlgorithmState.ResultFound);
    }

    public void setApplicationState(String state) {
        for (IAlgorithmStateChangedListener listener : this.algorithmStateListeners) {
            listener.stateChanged(state);
        }
    }

    private void notifyResultFound(String threadName, Matrix matrix) {
        for (IMatrixChangedListener listener : this.matrixChangedListeners) {
            listener.resultFound(threadName, matrix);
        }
    }

    public boolean threadAlreadyFoundResult(String threadName) {
        return threadsWithResults.contains(threadName);
    }

    public void reset() {
        ThreadDataAggregator.abortAllThreads.set(false);
        ThreadDataAggregator.resultFound.set(false);
        ThreadDataAggregator.threadsWithResults = Collections.synchronizedList(new ArrayList());
        this.setApplicationState(AlgorithmState.Waiting);
    }

    public void registerMatrixChangedListener(IMatrixChangedListener listener) {
        if (!this.matrixChangedListeners.contains(listener)) {
            this.matrixChangedListeners.add(listener);
        }
    }

    public void registerStateChangedListener(IAlgorithmStateChangedListener listener) {
        if (!this.algorithmStateListeners.contains(listener))
            this.algorithmStateListeners.add(listener);
    }

    public void removeStateChangedListener(IAlgorithmStateChangedListener listener) {
        if (this.algorithmStateListeners.contains(listener))
            this.algorithmStateListeners.remove(listener);
    }

    public void removeMatrixChangedListener(IMatrixChangedListener listener) {
        if (this.matrixChangedListeners.contains(listener)) {
            this.matrixChangedListeners.remove(listener);
        }
    }
}
