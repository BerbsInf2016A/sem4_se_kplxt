package hadamard;

import org.junit.Assert;
import org.junit.Test;

import java.util.BitSet;
import java.util.concurrent.Executors;

/**
 * Class for the sylvester algorithm strategy tests.
 */
public class SylvesterAlgorithmStrategyTest {

    /**
     * Tests if the sylvester algorithm runs for a matrix dimension eight and returns a valid result.
     */
    @Test
    public void SylvesterAlgorithmStrategy_RunsForDimensionEightAndReturnsValidResult() {
        SylvesterAlgorithmStrategy sylvesterAlgorithmStrategy = new SylvesterAlgorithmStrategy();
        Configuration.instance.dimension = 8;
        TestMatrixListener testMatrixListener = new TestMatrixListener();

        ThreadDataAggregator threadDataAggregator = new ThreadDataAggregator();
        threadDataAggregator.registerMatrixChangedListener(testMatrixListener);

        sylvesterAlgorithmStrategy.run(threadDataAggregator);

        Assert.assertTrue("Should be true.", testMatrixListener.isResultFound());

        Matrix resultMatrix = (Matrix) testMatrixListener.getResultMatrices().toArray()[0];

        Assert.assertTrue("Should be true", Helpers.isIdentity(resultMatrix.times(resultMatrix.transpose())));

        threadDataAggregator.reset();
    }

    /**
     * Test the sylvester algorithm function if it can run for a certain dimension.
     */
    @Test
    public void SylvesterAlgorithmStrategy_CanExecutorForDimension() {
        TestSylvesterAlgorithmStrategy testSylvesterAlgorithm = new TestSylvesterAlgorithmStrategy();
        testSylvesterAlgorithm.createExecutorPool();

        Assert.assertTrue("Should be true!", testSylvesterAlgorithm.canExecuteForDimension(32));
        Assert.assertTrue("Should be true!", testSylvesterAlgorithm.canExecuteForDimension(64));
        Assert.assertTrue("Should be true!", testSylvesterAlgorithm.canExecuteForDimension(1));
        Assert.assertTrue("Should be true!", testSylvesterAlgorithm.canExecuteForDimension(128));
        Assert.assertTrue("Should be true!", testSylvesterAlgorithm.canExecuteForDimension(256));

        Assert.assertFalse("Should be false!", testSylvesterAlgorithm.canExecuteForDimension(18));
        Assert.assertFalse("Should be false!", testSylvesterAlgorithm.canExecuteForDimension(24));
        Assert.assertFalse("Should be false!", testSylvesterAlgorithm.canExecuteForDimension(20));
        Assert.assertFalse("Should be false!", testSylvesterAlgorithm.canExecuteForDimension(12));
        Assert.assertFalse("Should be false!", testSylvesterAlgorithm.canExecuteForDimension(12323));
    }

    /**
     * Tests if the sylvester algorithm function for generation matrices returns a valid matrix.
     */
    @Test
    public void SylvesterAlgorithmStrategy_Matrix_Two() {
        TestSylvesterAlgorithmStrategy testSylvesterAlgorithm = new TestSylvesterAlgorithmStrategy();
        testSylvesterAlgorithm.createExecutorPool();
        testSylvesterAlgorithm.setAggregator(new ThreadDataAggregator());

        Matrix Matrix = new Matrix(1);

        Matrix secondMatrix = testSylvesterAlgorithm.generateNextSizeMatrix(Matrix);

        BitSet firstColumn = new BitSet();
        firstColumn.set(0);
        firstColumn.set(1);

        BitSet secondColumn = new BitSet();
        secondColumn.set(0);
        secondColumn.clear(1);

        Matrix expectedMatrix = new Matrix(2);
        expectedMatrix.setColumn(firstColumn, 0);
        expectedMatrix.setColumn(secondColumn, 1);

        Assert.assertTrue("Should be true.", expectedMatrix.equals(secondMatrix));
    }

    /**
     * Tests if the sylvester algorithm function for generation matrices returns a valid matrix.
     */
    @Test
    public void SylvesterAlgorithmStrategy_Matrix_Four() {
        TestSylvesterAlgorithmStrategy testSylvesterAlgorithm = new TestSylvesterAlgorithmStrategy();
        testSylvesterAlgorithm.createExecutorPool();
        testSylvesterAlgorithm.setAggregator(new ThreadDataAggregator());

        Matrix Matrix = new Matrix(1);

        Matrix secondMatrix = testSylvesterAlgorithm.generateNextSizeMatrix(Matrix);
        Matrix fourthMatrix = testSylvesterAlgorithm.generateNextSizeMatrix(secondMatrix);

        BitSet firstColumn = new BitSet();
        firstColumn.set(0);
        firstColumn.set(1);
        firstColumn.set(2);
        firstColumn.set(3);

        BitSet secondColumn = new BitSet();
        secondColumn.set(0);
        secondColumn.clear(1);
        secondColumn.set(2);
        secondColumn.clear(3);

        BitSet thirdColumn = new BitSet();
        thirdColumn.set(0);
        thirdColumn.set(1);
        thirdColumn.clear(2);
        thirdColumn.clear(3);

        BitSet fourthColumn = new BitSet();
        fourthColumn.set(0);
        fourthColumn.clear(1);
        fourthColumn.clear(2);
        fourthColumn.set(3);

        Matrix expectedMatrix = new Matrix(4);
        expectedMatrix.setColumn(firstColumn, 0);
        expectedMatrix.setColumn(secondColumn, 1);
        expectedMatrix.setColumn(thirdColumn, 2);
        expectedMatrix.setColumn(fourthColumn, 3);


        Assert.assertTrue("Should be true.", expectedMatrix.equals(fourthMatrix));
        Assert.assertTrue("Should be true.", Helpers.isIdentity(fourthMatrix.times(fourthMatrix.transpose())));
    }

    /**
     * Tests if the sylvester algorithm works for a matrix with the size of eight.
     */
    @Test
    public void SylvesterAlgorithmStrategy_Matrix_EightIsHadamard() {
        TestSylvesterAlgorithmStrategy testSylvesterAlgorithm = new TestSylvesterAlgorithmStrategy();
        testSylvesterAlgorithm.createExecutorPool();
        testSylvesterAlgorithm.setAggregator(new ThreadDataAggregator());

        Matrix Matrix = new Matrix(1);


        for (int i = 0; i < 3; i++)
            Matrix = testSylvesterAlgorithm.generateNextSizeMatrix(Matrix);

        Assert.assertTrue("Should be true.", Helpers.isIdentity(Matrix.times(Matrix.transpose())));
    }

    /**
     * Tests if the sylvester algorithm works for a matrix with the size of sixteen.
     */
    @Test
    public void SylvesterAlgorithmStrategy_Matrix_SixteenIsHadamard() {
        TestSylvesterAlgorithmStrategy testSylvesterAlgorithm = new TestSylvesterAlgorithmStrategy();
        testSylvesterAlgorithm.createExecutorPool();
        testSylvesterAlgorithm.setAggregator(new ThreadDataAggregator());

        Matrix Matrix = new Matrix(1);


        for (int i = 0; i < 4; i++)
            Matrix = testSylvesterAlgorithm.generateNextSizeMatrix(Matrix);


        Assert.assertTrue("Should be true.", Helpers.isIdentity(Matrix.times(Matrix.transpose())));
    }

    /**
     * Tests if the sylvester algorithm works for a matrix with the size of 512.
     * This test takes a while because of the validation at the end.
     */
    @Test
    public void SylvesterAlgorithmStrategy_Matrix_512IsHadamard() {
        TestSylvesterAlgorithmStrategy testSylvesterAlgorithm = new TestSylvesterAlgorithmStrategy();
        testSylvesterAlgorithm.createExecutorPool();
        testSylvesterAlgorithm.setAggregator(new ThreadDataAggregator());

        Matrix Matrix = new Matrix(1);


        for (int i = 0; i < 9; i++)
            Matrix = testSylvesterAlgorithm.generateNextSizeMatrix(Matrix);


        Assert.assertTrue("Should be true.", Helpers.isIdentity(Matrix.times(Matrix.transpose())));
    }

    /**
     * A SylvesterAlgorithmStrategy for tests.
     */
    private class TestSylvesterAlgorithmStrategy extends SylvesterAlgorithmStrategy {

        /**
         * Set the aggregator.
         *
         * @param aggregator The aggregator to set.
         */
        public void setAggregator(ThreadDataAggregator aggregator) {
            this.threadDataAggregator = aggregator;
        }

        /**
         * Create the executor pool.
         */
        private void createExecutorPool() {
            executorPool = Executors.newFixedThreadPool(Configuration.instance.maximumNumberOfThreads);
        }
    }
}
