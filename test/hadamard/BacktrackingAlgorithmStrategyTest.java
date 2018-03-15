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
}
