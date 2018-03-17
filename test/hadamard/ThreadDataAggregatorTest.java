package hadamard;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.BitSet;

/**
 * Class for the thread data aggregator tests.
 */
public class ThreadDataAggregatorTest {

    /**
     * Creates a test matrix.
     *
     * @return The test matrix.
     */
    private Matrix createTestMatrix() {
        Matrix testMatrix = new Matrix(4);
        testMatrix.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1111", 2)).toByteArray()), 0);
        testMatrix.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1100", 2)).toByteArray()), 1);
        testMatrix.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1110", 2)).toByteArray()), 2);
        testMatrix.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1011", 2)).toByteArray()), 3);

        return testMatrix;
    }

    /**
     * Tests the thread data aggregator if it updates the matrix and notifies the listener for the change.
     */
    @Test
    public void ThreadDataAggregator_UpdateMatrix() {
        ThreadDataAggregator threadDataAggregator = new ThreadDataAggregator();
        ThreadDataAggregatorTestMatrixListener threadDataAggregatorTestMatrixListener = new ThreadDataAggregatorTestMatrixListener();

        threadDataAggregator.registerMatrixChangedListener(threadDataAggregatorTestMatrixListener);

        Matrix matrix = createTestMatrix();

        threadDataAggregator.updateMatrix("Thread1", matrix);

        Assert.assertTrue("Should be true.", threadDataAggregatorTestMatrixListener.getMatrix().equals(matrix));
    }

    /**
     * Tests the thread data aggregator if it notifies the listener for a changed column in the matrix.
     */
    @Test
    public void ThreadDataAggregator_UpdateMatrixColumn() {
        ThreadDataAggregator threadDataAggregator = new ThreadDataAggregator();
        ThreadDataAggregatorTestMatrixListener threadDataAggregatorTestMatrixListener = new ThreadDataAggregatorTestMatrixListener();

        threadDataAggregator.registerMatrixChangedListener(threadDataAggregatorTestMatrixListener);

        Matrix matrix = createTestMatrix();

        threadDataAggregator.updateMatrix("Thread1", matrix);
        Assert.assertTrue("Should be true.", threadDataAggregatorTestMatrixListener.getMatrix().equals(matrix));

        matrix.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1011", 2)).toByteArray()), 3);
        threadDataAggregator.updateMatrixColumn("Thread1", 3, BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1011", 2)).toByteArray()));
        Assert.assertTrue("Should be true.", threadDataAggregatorTestMatrixListener.getMatrix().equals(matrix));
    }

    /**
     * Tests the thread data aggregator if its setting the result flag correctly and also if it notifies the listener for a result.
     */
    @Test
    public void ThreadDataAggregator_SetResult() {
        ThreadDataAggregator threadDataAggregator = new ThreadDataAggregator();
        ThreadDataAggregatorTestMatrixListener threadDataAggregatorTestMatrixListener = new ThreadDataAggregatorTestMatrixListener();

        threadDataAggregator.registerMatrixChangedListener(threadDataAggregatorTestMatrixListener);

        Matrix matrix = createTestMatrix();

        threadDataAggregator.setResult("Thread1", matrix);

        Assert.assertTrue("Should be true.", threadDataAggregatorTestMatrixListener.isResultFound());
        Assert.assertTrue("Should be true.", threadDataAggregatorTestMatrixListener.getMatrix().equals(matrix));
    }

    /**
     * Tests the thread data aggregator if it notifies the Algorithm State Changed listener correctly.
     */
    @Test
    public void ThreadDataAggregator_AlgorithmStateListeners() {
        ThreadDataAggregator threadDataAggregator = new ThreadDataAggregator();
        TestAlgorithmStateChangedListener testAlgorithmStateChangedListener = new TestAlgorithmStateChangedListener();

        threadDataAggregator.registerStateChangedListener(testAlgorithmStateChangedListener);

        threadDataAggregator.setAlgorithmState(AlgorithmState.Running);
        Assert.assertEquals("Should be the same.", "Running", testAlgorithmStateChangedListener.getState());

        threadDataAggregator.setAlgorithmState(AlgorithmState.Waiting);
        Assert.assertEquals("Should be the same.", "Waiting", testAlgorithmStateChangedListener.getState());

        threadDataAggregator.setAlgorithmState(AlgorithmState.Validating);
        Assert.assertEquals("Should be the same.", "Validating", testAlgorithmStateChangedListener.getState());

        threadDataAggregator.setAlgorithmState(AlgorithmState.ResultFound);
        Assert.assertEquals("Should be the same.", "Result found", testAlgorithmStateChangedListener.getState());
    }

    /**
     * Tests the thread data aggregator if it has added the result matrix to the list and not a changed one.
     */
    @Test
    public void ThreadDataAggregator_ThreadAlreadyFoundResult() {
        ThreadDataAggregator threadDataAggregator = new ThreadDataAggregator();
        Matrix testMatrix = new Matrix(1);

        String resultThreadName = "ResultThread1";

        String updateThreadName = "UpdateThread1";

        threadDataAggregator.setResult(resultThreadName, testMatrix);
        threadDataAggregator.updateMatrix(updateThreadName, testMatrix);

        Assert.assertTrue("Should be true.", threadDataAggregator.threadsWithResults.contains(resultThreadName));
        Assert.assertFalse("Should be false.", threadDataAggregator.threadsWithResults.contains(updateThreadName));
    }

    /**
     * Tests the thread data aggregator for resetting all necessary values.
     */
    @Test
    public void ThreadDataAggregator_Reset() {
        ThreadDataAggregator threadDataAggregator = new ThreadDataAggregator();
        TestAlgorithmStateChangedListener testAlgorithmStateChangedListener = new TestAlgorithmStateChangedListener();

        threadDataAggregator.registerStateChangedListener(testAlgorithmStateChangedListener);

        threadDataAggregator.abortAllThreads.set(true);
        threadDataAggregator.resultFound.set(true);
        threadDataAggregator.setAlgorithmState(AlgorithmState.Running);

        Assert.assertTrue("Should be true.", threadDataAggregator.abortAllThreads.get());
        Assert.assertTrue("Should be true.", threadDataAggregator.resultFound.get());
        Assert.assertTrue("Should be true.", testAlgorithmStateChangedListener.getState().equals(AlgorithmState.Running));

        threadDataAggregator.reset();

        Assert.assertFalse("Should be false.", threadDataAggregator.abortAllThreads.get());
        Assert.assertFalse("Should be false.", threadDataAggregator.resultFound.get());
        Assert.assertTrue("Should be true.", testAlgorithmStateChangedListener.getState().equals(AlgorithmState.Waiting));
    }

    /**
     * A matrix listener for the thread data aggregator tests.
     */
    private class ThreadDataAggregatorTestMatrixListener implements IMatrixChangedListener {
        /**
         * The matrix.
         */
        private Matrix matrix;
        /**
         * Boolean indicating if a result was found.
         */
        private boolean resultFound = false;

        /**
         * Gets the matrix.
         *
         * @return The matrix.
         */
        public Matrix getMatrix() {
            return matrix;
        }

        /**
         * Gets the boolean indication if a result was found.
         * @return Boolean indicating if a result was found.
         */
        public boolean isResultFound() {
            return resultFound;
        }

        /**
         * Called when the matrix was changed.
         *
         * @param threadName    The name of the thread.
         * @param changedMatrix The changed matrix.
         */
        public void matrixChanged(String threadName, Matrix changedMatrix) {
            this.matrix = changedMatrix;
        }

        /**
         * Called when a column was changed.
         *
         * @param threadName  The name of the thread.
         * @param columnIndex The column index.
         * @param column      The column.
         */
        public void matrixColumnChanged(String threadName, int columnIndex, BitSet column) {
            this.matrix.setColumn(column, columnIndex);
        }

        /**
         * Called whe a result was found.
         *
         * @param threadName   The thread name.
         * @param resultMatrix The result matrix.
         */
        public void resultFound(String threadName, Matrix resultMatrix) {
            this.matrix = resultMatrix;
            this.resultFound = true;
        }
    }
}
