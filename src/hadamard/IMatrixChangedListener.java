package hadamard;

import java.util.BitSet;

/**
 * Interface for the Matrix Changed Listener.
 */
public interface IMatrixChangedListener {
    /**
     * Updates the Matrix for changes.
     *
     * @param threadName The name of the Thread.
     * @param changedMatrix The changed Matrix.
     */
    void matrixChanged(String threadName, Matrix changedMatrix);

    /**
     * Updates the Matrix Column for changes.
     *
     * @param threadName The name of the thread.
     * @param columnIndex The Column index.
     * @param column The Column.
     */
    void matrixColumnChanged(String threadName, int columnIndex, BitSet column);

    /**
     * Sets the result Matrix.
     * @param threadName The Thread Name.
     * @param resultMatrix The result Matrix.
     */
    void resultFound(String threadName, Matrix resultMatrix);
}
