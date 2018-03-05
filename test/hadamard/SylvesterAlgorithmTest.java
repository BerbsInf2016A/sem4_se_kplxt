package hadamard;

import org.junit.Assert;
import org.junit.Test;

public class SylvesterAlgorithmTest {
    @Test
    public void SylvesterAlgorithmTest_CanExecutorForDimension() {
        SylvesterAlgorithm sylvesterAlgorithm = new SylvesterAlgorithm();
        Assert.assertTrue("Should be true!", sylvesterAlgorithm.canExecutorForDimension(32));
        Assert.assertTrue("Should be true!", sylvesterAlgorithm.canExecutorForDimension(64));
        Assert.assertTrue("Should be true!", sylvesterAlgorithm.canExecutorForDimension(1));
        Assert.assertTrue("Should be true!", sylvesterAlgorithm.canExecutorForDimension(128));
        Assert.assertTrue("Should be true!", sylvesterAlgorithm.canExecutorForDimension(256));

        Assert.assertFalse("Should be false!", sylvesterAlgorithm.canExecutorForDimension(18));
        Assert.assertFalse("Should be false!", sylvesterAlgorithm.canExecutorForDimension(24));
        Assert.assertFalse("Should be false!", sylvesterAlgorithm.canExecutorForDimension(20));
        Assert.assertFalse("Should be false!", sylvesterAlgorithm.canExecutorForDimension(12));
        Assert.assertFalse("Should be false!", sylvesterAlgorithm.canExecutorForDimension(12323));
    }
}
