package hadamard;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Class for the Thread Data Aggregator.
 * Used for reporting the Matrix changes and results, delivered by a Thread, to the UI.
 */
public class ThreadDataAggregator {
    /**
     * Boolean indicating if the Thread found a Valid Result.
     */
    public static AtomicBoolean resultFound = new AtomicBoolean();
    /**
     * Boolean indicating if all the Threads need to be aborted.
     */
    public static AtomicBoolean abortAllThreads = new AtomicBoolean();
    /**
     * List of all Thread names that have found an Result.
     */
    public static List<String> threadsWithResults = Collections.synchronizedList(new ArrayList());

    /**
     * List of the Matrix Changed listeners.
     */
    private List<IMatrixChangedListener> matrixChangedListeners;
    /**
     * List of the Algorithm State listeners.
     */
    private List<IAlgorithmStateChangedListener> algorithmStateListeners;

    /**
     * Constructor for the Thread Data Aggregator.
     */
    public ThreadDataAggregator() {
        this.matrixChangedListeners = new ArrayList<>();
        this.algorithmStateListeners = new ArrayList<>();
    }

    /**
     * Notifies the listeners if the Matrix was updated.
     *
     * @param threadName The name of the Thread.
     * @param matrix The updated Matrix.
     */
    public void updateMatrix(String threadName, Matrix matrix) {
        if (Configuration.instance.printDebugMessages)
            System.out.println(matrix.getUIDebugStringRepresentation());
        for (IMatrixChangedListener listener : this.matrixChangedListeners) {
            listener.matrixChanged(threadName, matrix);
        }
    }

    /**
     * Notifies the listeners for a Column change in the Matrix.
     *
     * @param threadName The name of the Thread.
     * @param columnIndex The Column index.
     * @param column The Column that was updated.
     */
    public void updateMatrixColumn(String threadName, int columnIndex, BitSet column) {
        for (IMatrixChangedListener listener : this.matrixChangedListeners) {
            listener.matrixColumnChanged(threadName, columnIndex, column);
        }
    }

    /**
     * Sets the result matrix and notifies the listeners that an result was found.
     * Also aborts all threads if the "Abort if the first result was found" flag is set.
     *
     * @param threadName The name of the Thread.
     * @param matrix The Result Matrix.
     */
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

    /**
     * Sets the Application State.
     *
     * @param state The state of the Application.
     */
    public void setApplicationState(String state) {
        for (IAlgorithmStateChangedListener listener : this.algorithmStateListeners) {
            listener.stateChanged(state);
        }
    }

    /**
     * Notifies the listeners for a found result Matrix.
     *
     * @param threadName The name of the Thread.
     * @param matrix The result Matrix.
     */
    private void notifyResultFound(String threadName, Matrix matrix) {
        for (IMatrixChangedListener listener : this.matrixChangedListeners) {
            listener.resultFound(threadName, matrix);
        }
    }

    /**
     * Checks if the thread name is present in the list of threads that found an result.
     *
     * @param threadName The name of the thread.
     * @return Boolean indicating if the Thread is present in the list of threads that found an result.
     */
    public boolean threadAlreadyFoundResult(String threadName) {
        return threadsWithResults.contains(threadName);
    }

    /**
     * Resets the thread data aggregator.
     * Resets all flags and the Application state.
     */
    public void reset() {
        ThreadDataAggregator.abortAllThreads.set(false);
        ThreadDataAggregator.resultFound.set(false);
        ThreadDataAggregator.threadsWithResults = Collections.synchronizedList(new ArrayList());
        this.setApplicationState(AlgorithmState.Waiting);
    }

    /**
     * Registers a Matrix Changed Listener.
     * 
     * @param listener The Matrix Changed listener.
     */
    public void registerMatrixChangedListener(IMatrixChangedListener listener) {
        if (!this.matrixChangedListeners.contains(listener)) {
            this.matrixChangedListeners.add(listener);
        }
    }

    /**
     * Registers a State Changed listener.
     *
     * @param listener The State Changed listener.
     */
    public void registerStateChangedListener(IAlgorithmStateChangedListener listener) {
        if (!this.algorithmStateListeners.contains(listener))
            this.algorithmStateListeners.add(listener);
    }

    /**
     * Removes a State Changed listener.
     *
     * @param listener The State Changed listener.
     */
    public void removeStateChangedListener(IAlgorithmStateChangedListener listener) {
        if (this.algorithmStateListeners.contains(listener))
            this.algorithmStateListeners.remove(listener);
    }

    /**
     * Removes a Matrix Changed Listener.
     *
     * @param listener The Matrix Changed listener.
     */
    public void removeMatrixChangedListener(IMatrixChangedListener listener) {
        if (this.matrixChangedListeners.contains(listener)) {
            this.matrixChangedListeners.remove(listener);
        }
    }
}
