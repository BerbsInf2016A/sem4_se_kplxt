package hadamard;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * A matrix listener for tests.
 */
public class TestMatrixListener implements IMatrixChangedListener {
    /**
     * The result matrices.
     */
    private List<Matrix> resultMatrices = new ArrayList<>();
    /**
     * True if a result is set.
     */
    private boolean resultFound = false;

    /**
     * Get the result matrices.
     *
     * @return
     */
    public List<Matrix> getResultMatrices() {
        return resultMatrices;
    }

    /**
     * True if a result has been found.
     *
     * @return True if a result has been found, false if not.
     */
    public boolean isResultFound() {
        return resultFound;
    }

    /**
     * Called when a matrix is changed.
     *
     * @param threadName    The name of the Thread.
     * @param changedMatrix The changed Matrix.
     */
    public void matrixChanged(String threadName, Matrix changedMatrix) {
        this.resultMatrices.add(changedMatrix);
    }

    /**
     * Called when a column of a matrix is changed.
     *
     * @param threadName  The name of the thread.
     * @param columnIndex The Column index.
     * @param column      The Column.
     */
    public void matrixColumnChanged(String threadName, int columnIndex, BitSet column) {
    }

    /**
     * Called when a result has been found.
     *
     * @param threadName    The Thread Name.
     * @param changedMatrix The result matrix.
     */
    public void resultFound(String threadName, Matrix changedMatrix) {
        this.resultMatrices.add(changedMatrix);
        this.resultFound = true;
    }
}
