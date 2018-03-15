package hadamard;

import org.junit.Assert;
import org.junit.Test;

public class BacktrackingAlgorithmStrategyTest {

    @Test
    public void BacktrackingAlgorithm_CanExecutorForDimension() {
        BacktrackingAlgorithmStrategy testBacktrackingAlgorithmStrategy = new BacktrackingAlgorithmStrategy();

        Assert.assertTrue("Should be true!", testBacktrackingAlgorithmStrategy.canExecuteForDimension(32));
        Assert.assertTrue("Should be true!", testBacktrackingAlgorithmStrategy.canExecuteForDimension(64));
        Assert.assertTrue("Should be true!", testBacktrackingAlgorithmStrategy.canExecuteForDimension(1));
        Assert.assertTrue("Should be true!", testBacktrackingAlgorithmStrategy.canExecuteForDimension(128));
        Assert.assertTrue("Should be true!", testBacktrackingAlgorithmStrategy.canExecuteForDimension(256));
        Assert.assertTrue("Should be true!", testBacktrackingAlgorithmStrategy.canExecuteForDimension(24));
        Assert.assertTrue("Should be true!", testBacktrackingAlgorithmStrategy.canExecuteForDimension(20));

        Assert.assertFalse("Should be false!", testBacktrackingAlgorithmStrategy.canExecuteForDimension(18));
        Assert.assertFalse("Should be false!", testBacktrackingAlgorithmStrategy.canExecuteForDimension(1234));
        Assert.assertFalse("Should be false!", testBacktrackingAlgorithmStrategy.canExecuteForDimension(5555));
        Assert.assertFalse("Should be false!", testBacktrackingAlgorithmStrategy.canExecuteForDimension(1023));
        Assert.assertFalse("Should be false!", testBacktrackingAlgorithmStrategy.canExecuteForDimension(12323));
    }

    @Test
    public void Backtracking_DimensionEight_NumberOfResultsEqualsToNumberOfThreads() {
        ThreadDataAggregator threadDataAggregator = new ThreadDataAggregator();
        BacktrackingAlgorithmStrategy testee = new BacktrackingAlgorithmStrategy();
        Configuration.instance.dimension = 8;
        Configuration.instance.abortAfterFirstResult = false;
        Configuration.instance.simulateSteps = false;

        TestMatrixListener listener = new TestMatrixListener();
        threadDataAggregator.registerMatrixChangedListener(listener);

        try{
            testee.run(threadDataAggregator);
        } catch (RuntimeException e){
            // Hide the exceptions: Threads can be interrupted and stuff like that.
        }

        Assert.assertEquals("The number of results should be equal to the number of configured threads",
                listener.getResultMatrices().size(), Configuration.instance.maximumNumberOfThreads);

        for (Matrix matrix : listener.getResultMatrices() ) {
            Assert.assertTrue("Result matrix should be a hadamard matrix", Helpers.isIdentity(matrix.times(matrix.transpose())));
        }
    }

    @Test
    public void Backtracking_DimensionEight_NumberOfResultsEqualsOne() {
        ThreadDataAggregator threadDataAggregator = new ThreadDataAggregator();
        BacktrackingAlgorithmStrategy testee = new BacktrackingAlgorithmStrategy();
        Configuration.instance.dimension = 8;
        Configuration.instance.abortAfterFirstResult = true;
        Configuration.instance.simulateSteps = false;

        TestMatrixListener listener = new TestMatrixListener();
        threadDataAggregator.registerMatrixChangedListener(listener);

        try{
            testee.run(threadDataAggregator);
        } catch (RuntimeException e){
            // Hide the exceptions: Threads can be interrupted and stuff like that.
        }

        Assert.assertEquals("The number of results should be one",
                listener.getResultMatrices().size(), 1);

        for (Matrix matrix : listener.getResultMatrices() ) {
            Assert.assertTrue("Result matrix should be a hadamard matrix", Helpers.isIdentity(matrix.times(matrix.transpose())));
        }
    }

    @Test
    public void Backtracking_DimensionOne_NumberOfResultsEqualsOne() {
        ThreadDataAggregator threadDataAggregator = new ThreadDataAggregator();
        BacktrackingAlgorithmStrategy testee = new BacktrackingAlgorithmStrategy();
        Configuration.instance.dimension = 1;
        Configuration.instance.abortAfterFirstResult = true;
        Configuration.instance.simulateSteps = false;

        TestMatrixListener listener = new TestMatrixListener();
        threadDataAggregator.registerMatrixChangedListener(listener);

        try{
            testee.run(threadDataAggregator);
        } catch (RuntimeException e){
            // Hide the exceptions: Threads can be interrupted and stuff like that.
        }

        Assert.assertEquals("The number of results should be one",
                listener.getResultMatrices().size(), 1);

        for (Matrix matrix : listener.getResultMatrices() ) {
            Assert.assertTrue("Result matrix should be a hadamard matrix", Helpers.isIdentity(matrix.times(matrix.transpose())));
        }
    }
}
