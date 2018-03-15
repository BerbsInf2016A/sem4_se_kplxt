package hadamard;

import org.junit.Assert;
import org.junit.Test;

public class BacktrackingAlgorithmTest {

    @Test
    public void BacktrackingAlgorithm_CanExecutorForDimension() {
        BacktrackingAlgorithm testBacktrackingAlgorithm = new BacktrackingAlgorithm();

        Assert.assertTrue("Should be true!", testBacktrackingAlgorithm.canExecutorForDimension(32));
        Assert.assertTrue("Should be true!", testBacktrackingAlgorithm.canExecutorForDimension(64));
        Assert.assertTrue("Should be true!", testBacktrackingAlgorithm.canExecutorForDimension(1));
        Assert.assertTrue("Should be true!", testBacktrackingAlgorithm.canExecutorForDimension(128));
        Assert.assertTrue("Should be true!", testBacktrackingAlgorithm.canExecutorForDimension(256));
        Assert.assertTrue("Should be true!", testBacktrackingAlgorithm.canExecutorForDimension(24));
        Assert.assertTrue("Should be true!", testBacktrackingAlgorithm.canExecutorForDimension(20));

        Assert.assertFalse("Should be false!", testBacktrackingAlgorithm.canExecutorForDimension(18));
        Assert.assertFalse("Should be false!", testBacktrackingAlgorithm.canExecutorForDimension(1234));
        Assert.assertFalse("Should be false!", testBacktrackingAlgorithm.canExecutorForDimension(5555));
        Assert.assertFalse("Should be false!", testBacktrackingAlgorithm.canExecutorForDimension(1023));
        Assert.assertFalse("Should be false!", testBacktrackingAlgorithm.canExecutorForDimension(12323));
    }
}
