package hadamard;

import java.util.BitSet;

/**
 * Interface for the matrix changed listener.
 */
public interface IMatrixChangedListener {
    /**
     * Updates the matrix for changes.
     *
     * @param threadName The name of the thread.
     * @param changedMatrix The changed matrix.
     */
    void matrixChanged(String threadName, Matrix changedMatrix);

    /**
     * Updates the matrix column for changes.
     *
     * @param threadName The name of the thread.
     * @param columnIndex The column index.
     * @param column The column.
     */
    void matrixColumnChanged(String threadName, int columnIndex, BitSet column);

    /**
     * Sets the result matrix.
     * @param threadName The thread name.
     * @param resultMatrix The result matrix.
     */
    void resultFound(String threadName, Matrix resultMatrix);
}
