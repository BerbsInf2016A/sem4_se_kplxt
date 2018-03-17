package hadamard;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;

public class ThreadDataAggregatorTest {

    private Matrix createTestMatrix() {
        Matrix testMatrix = new Matrix(4);
        testMatrix.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1111", 2)).toByteArray()), 0);
        testMatrix.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1100", 2)).toByteArray()), 1);
        testMatrix.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1110", 2)).toByteArray()), 2);
        testMatrix.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1011", 2)).toByteArray()), 3);

        return testMatrix;
    }

    @Test
    public void ThreadDataAggregator_UpdateMatrix() {
        ThreadDataAggregator threadDataAggregator = new ThreadDataAggregator();
        TestMatrixListener testMatrixListener = new TestMatrixListener();

        threadDataAggregator.registerMatrixChangedListener(testMatrixListener);

        Matrix matrix = createTestMatrix();

        threadDataAggregator.updateMatrix("Thread1", matrix);

        Assert.assertTrue("Should be true.", testMatrixListener.getMatrix().equals(matrix));
    }

    @Test
    public void ThreadDataAggregator_UpdateMatrixColumn() {
        ThreadDataAggregator threadDataAggregator = new ThreadDataAggregator();
        TestMatrixListener testMatrixListener = new TestMatrixListener();

        threadDataAggregator.registerMatrixChangedListener(testMatrixListener);

        Matrix matrix = createTestMatrix();

        threadDataAggregator.updateMatrix("Thread1", matrix);
        Assert.assertTrue("Should be true.", testMatrixListener.getMatrix().equals(matrix));

        matrix.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1011", 2)).toByteArray()), 3);
        threadDataAggregator.updateMatrixColumn("Thread1", 3, BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1011", 2)).toByteArray()));
        Assert.assertTrue("Should be true.", testMatrixListener.getMatrix().equals(matrix));
    }

    @Test
    public void ThreadDataAggregator_SetResult() {
        ThreadDataAggregator threadDataAggregator = new ThreadDataAggregator();
        TestMatrixListener testMatrixListener = new TestMatrixListener();

        threadDataAggregator.registerMatrixChangedListener(testMatrixListener);

        Matrix matrix = createTestMatrix();

        threadDataAggregator.setResult("Thread1", matrix);

        Assert.assertTrue("Should be true.", testMatrixListener.isResultFound());
        Assert.assertTrue("Should be true.", testMatrixListener.getMatrix().equals(matrix));
    }

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

    private class TestMatrixListener implements IMatrixChangedListener {
        private Matrix matrix;
        private boolean resultFound = false;

        public Matrix getMatrix() {
            return matrix;
        }

        public boolean isResultFound() {
            return resultFound;
        }

        public void matrixChanged(String threadName, Matrix changedMatrix) {
            this.matrix = changedMatrix;
        }

        public void matrixColumnChanged(String threadName, int columnIndex, BitSet column) {
            this.matrix.setColumn(column, columnIndex);
        }

        public void resultFound(String threadName, Matrix changedMatrix) {
            this.matrix = changedMatrix;
            this.resultFound = true;
        }
    }
}
