package hadamard;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.BitSet;

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
    }

    @Test
    public void ThreadDataAggregator_Reset() {
    }

    private class TestAlgorithmStateChangedListener implements IAlgorithmStateChangedListener {
        private String state;

        public String getState() {
            return state;
        }

        public void stateChanged(String newState) {
            this.state = newState;
        }
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
