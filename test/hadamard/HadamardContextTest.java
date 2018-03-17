package hadamard;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CancellationException;

public class HadamardContextTest {

    @Test
    public void HadamardContext_ExecuteStrategy() {
        BacktrackingAlgorithmStrategy backtrackingAlgorithmStrategy = new BacktrackingAlgorithmStrategy();

        ThreadDataAggregator threadDataAggregator = new ThreadDataAggregator();

        TestMatrixListener testMatrixListener = new TestMatrixListener();
        TestAlgorithmStateChangedListener testAlgorithmStateChangedListener = new TestAlgorithmStateChangedListener();

        threadDataAggregator.registerMatrixChangedListener(testMatrixListener);
        threadDataAggregator.registerStateChangedListener(testAlgorithmStateChangedListener);

        Configuration.instance.dimension = 4;

        HadamardContext hadamardContext = new HadamardContext(backtrackingAlgorithmStrategy);

        try {
            hadamardContext.executeStrategy(threadDataAggregator);
        } catch (Exception e) {
            // Hide the exceptions: Threads can be interrupted and stuff like that.
        }

        Assert.assertTrue("Should be true.", testMatrixListener.isResultFound());
        Assert.assertTrue("Should be true.", testAlgorithmStateChangedListener.getState().equals(AlgorithmState.ResultFound));
    }

    @Test
    public void HadamardContext_CanExecuteForDimension() {
        BacktrackingAlgorithmStrategy backtrackingAlgorithmStrategy = new BacktrackingAlgorithmStrategy();
        SylvesterAlgorithmStrategy sylvesterAlgorithmStrategy = new SylvesterAlgorithmStrategy();

        HadamardContext hadamardContext = new HadamardContext(backtrackingAlgorithmStrategy);

        Assert.assertTrue("Should be true.", hadamardContext.canExecuteForDimension(1));
        Assert.assertTrue("Should be true.", hadamardContext.canExecuteForDimension(2));
        Assert.assertTrue("Should be true.", hadamardContext.canExecuteForDimension(4));
        Assert.assertTrue("Should be true.", hadamardContext.canExecuteForDimension(8));
        Assert.assertTrue("Should be true.", hadamardContext.canExecuteForDimension(12));
        Assert.assertTrue("Should be true.", hadamardContext.canExecuteForDimension(16));
        Assert.assertTrue("Should be true.", hadamardContext.canExecuteForDimension(668));

        Assert.assertFalse("Should be false.", hadamardContext.canExecuteForDimension(6));
        Assert.assertFalse("Should be false.", hadamardContext.canExecuteForDimension(22));
        Assert.assertFalse("Should be false.", hadamardContext.canExecuteForDimension(9));
        Assert.assertFalse("Should be false.", hadamardContext.canExecuteForDimension(0));

        hadamardContext = new HadamardContext(sylvesterAlgorithmStrategy);

        Assert.assertTrue("Should be true.", hadamardContext.canExecuteForDimension(1));
        Assert.assertTrue("Should be true.", hadamardContext.canExecuteForDimension(2));
        Assert.assertTrue("Should be true.", hadamardContext.canExecuteForDimension(4));
        Assert.assertTrue("Should be true.", hadamardContext.canExecuteForDimension(8));
        Assert.assertTrue("Should be true.", hadamardContext.canExecuteForDimension(16));
        Assert.assertTrue("Should be true.", hadamardContext.canExecuteForDimension(128));

        Assert.assertFalse("Should be false.", hadamardContext.canExecuteForDimension(12));
        Assert.assertFalse("Should be false.", hadamardContext.canExecuteForDimension(668));
        Assert.assertFalse("Should be false.", hadamardContext.canExecuteForDimension(6));
        Assert.assertFalse("Should be false.", hadamardContext.canExecuteForDimension(22));
        Assert.assertFalse("Should be false.", hadamardContext.canExecuteForDimension(9));
        Assert.assertFalse("Should be false.", hadamardContext.canExecuteForDimension(0));
    }
}