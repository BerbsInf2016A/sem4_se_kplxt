package hadamard;

import org.junit.Assert;
import org.junit.Test;

import java.util.BitSet;
import java.util.concurrent.Executors;

public class SylvesterAlgorithmTest {
    private class TestSylvesterAlgorithm extends SylvesterAlgorithm {
        private void createExecutorPool() {
            executorPool = Executors.newFixedThreadPool(Configuration.instance.maximumNumberOfThreads);
        }
    }

    @Test
    public void SylvesterAlgorithmTest_CanExecutorForDimension() {
        TestSylvesterAlgorithm testSylvesterAlgorithm = new TestSylvesterAlgorithm();
        testSylvesterAlgorithm.createExecutorPool();

        Assert.assertTrue("Should be true!", testSylvesterAlgorithm.canExecutorForDimension(32));
        Assert.assertTrue("Should be true!", testSylvesterAlgorithm.canExecutorForDimension(64));
        Assert.assertTrue("Should be true!", testSylvesterAlgorithm.canExecutorForDimension(1));
        Assert.assertTrue("Should be true!", testSylvesterAlgorithm.canExecutorForDimension(128));
        Assert.assertTrue("Should be true!", testSylvesterAlgorithm.canExecutorForDimension(256));

        Assert.assertFalse("Should be false!", testSylvesterAlgorithm.canExecutorForDimension(18));
        Assert.assertFalse("Should be false!", testSylvesterAlgorithm.canExecutorForDimension(24));
        Assert.assertFalse("Should be false!", testSylvesterAlgorithm.canExecutorForDimension(20));
        Assert.assertFalse("Should be false!", testSylvesterAlgorithm.canExecutorForDimension(12));
        Assert.assertFalse("Should be false!", testSylvesterAlgorithm.canExecutorForDimension(12323));
    }

    @Test
    public void SylvesterMatrix_Two() {
        TestSylvesterAlgorithm testSylvesterAlgorithm = new TestSylvesterAlgorithm();
        testSylvesterAlgorithm.createExecutorPool();

        SylvesterMatrix sylvesterMatrix = new SylvesterMatrix(true);

        SylvesterMatrix secondSylvesterMatrix = testSylvesterAlgorithm.generateNextSizeMatrix(sylvesterMatrix);

        BitSet firstColumn = new BitSet();
        firstColumn.set(0);
        firstColumn.set(1);

        BitSet secondColumn = new BitSet();
        secondColumn.set(0);
        secondColumn.clear(1);

        Matrix expectedMatrix = new Matrix(2);
        expectedMatrix.setColumn(firstColumn, 0);
        expectedMatrix.setColumn(secondColumn, 1);

        Assert.assertTrue("Should be true.", expectedMatrix.equals(secondSylvesterMatrix));
    }

    @Test
    public void SylvesterMatrix_Four() {
        TestSylvesterAlgorithm testSylvesterAlgorithm = new TestSylvesterAlgorithm();
        testSylvesterAlgorithm.createExecutorPool();

        SylvesterMatrix sylvesterMatrix = new SylvesterMatrix(true);

        SylvesterMatrix secondSylvesterMatrix = testSylvesterAlgorithm.generateNextSizeMatrix(sylvesterMatrix);
        SylvesterMatrix fourthSylvesterMatrix = testSylvesterAlgorithm.generateNextSizeMatrix(secondSylvesterMatrix);

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

        Assert.assertTrue("Should be true.", expectedMatrix.equals(fourthSylvesterMatrix));
        Assert.assertTrue("Should be true.",  Helpers.isIdentity(fourthSylvesterMatrix.times(fourthSylvesterMatrix.transpose())));
    }

    @Test
    public void SylvesterMatrix_EightIsHadamardWithStartValue1() {
        TestSylvesterAlgorithm testSylvesterAlgorithm = new TestSylvesterAlgorithm();
        testSylvesterAlgorithm.createExecutorPool();

        SylvesterMatrix sylvesterMatrix = new SylvesterMatrix(true);

        for(int i=0; i<3; i++)
            sylvesterMatrix = testSylvesterAlgorithm.generateNextSizeMatrix(sylvesterMatrix);

        Assert.assertTrue("Should be true.", Helpers.isIdentity(sylvesterMatrix.times(sylvesterMatrix.transpose())));
    }

    @Test
    public void SylvesterMatrix_EightIsHadamardWithStartValueMinus1() {
        TestSylvesterAlgorithm testSylvesterAlgorithm = new TestSylvesterAlgorithm();
        testSylvesterAlgorithm.createExecutorPool();

        SylvesterMatrix sylvesterMatrix = new SylvesterMatrix(false);

        for(int i=0; i<3; i++)
            sylvesterMatrix = testSylvesterAlgorithm.generateNextSizeMatrix(sylvesterMatrix);

        Assert.assertTrue("Should be true.", Helpers.isIdentity(sylvesterMatrix.times(sylvesterMatrix.transpose())));
    }

    @Test
    public void SylvesterMatrix_SixteenIsHadamardWithStartValue1() {
        TestSylvesterAlgorithm testSylvesterAlgorithm = new TestSylvesterAlgorithm();
        testSylvesterAlgorithm.createExecutorPool();

        SylvesterMatrix sylvesterMatrix = new SylvesterMatrix(true);

        for(int i=0; i<4; i++)
            sylvesterMatrix = testSylvesterAlgorithm.generateNextSizeMatrix(sylvesterMatrix);

        Assert.assertTrue("Should be true.", Helpers.isIdentity(sylvesterMatrix.times(sylvesterMatrix.transpose())));
    }

    @Test
    public void SylvesterMatrix_512IsHadamardWithStartValueMinus1() {
        TestSylvesterAlgorithm testSylvesterAlgorithm = new TestSylvesterAlgorithm();
        testSylvesterAlgorithm.createExecutorPool();

        SylvesterMatrix sylvesterMatrix = new SylvesterMatrix(true);

        for(int i=0; i<9; i++)
            sylvesterMatrix = testSylvesterAlgorithm.generateNextSizeMatrix(sylvesterMatrix);

        Assert.assertTrue("Should be true.", Helpers.isIdentity(sylvesterMatrix.times(sylvesterMatrix.transpose())));
    }
}
