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
    public void SylvesterAlgorithm_CanExecutorForDimension() {
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
    public void SylvesterAlgorithm_Matrix_Two() {
        TestSylvesterAlgorithm testSylvesterAlgorithm = new TestSylvesterAlgorithm();
        testSylvesterAlgorithm.createExecutorPool();

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

    @Test
    public void SylvesterAlgorithm_Matrix_Four() {
        TestSylvesterAlgorithm testSylvesterAlgorithm = new TestSylvesterAlgorithm();
        testSylvesterAlgorithm.createExecutorPool();

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
        Assert.assertTrue("Should be true.",  Helpers.isIdentity(fourthMatrix.times(fourthMatrix.transpose())));
    }

    @Test
    public void SylvesterAlgorithm_Matrix_EightIsHadamard() {
        TestSylvesterAlgorithm testSylvesterAlgorithm = new TestSylvesterAlgorithm();
        testSylvesterAlgorithm.createExecutorPool();

        Matrix Matrix = new Matrix(1);

        for(int i=0; i<3; i++)
            Matrix = testSylvesterAlgorithm.generateNextSizeMatrix(Matrix);

        Assert.assertTrue("Should be true.", Helpers.isIdentity(Matrix.times(Matrix.transpose())));
    }

    @Test
    public void SylvesterAlgorithm_Matrix_SixteenIsHadamard() {
        TestSylvesterAlgorithm testSylvesterAlgorithm = new TestSylvesterAlgorithm();
        testSylvesterAlgorithm.createExecutorPool();

        Matrix Matrix = new Matrix(1);

        for(int i=0; i<4; i++)
            Matrix = testSylvesterAlgorithm.generateNextSizeMatrix(Matrix);

        Assert.assertTrue("Should be true.", Helpers.isIdentity(Matrix.times(Matrix.transpose())));
    }

    @Test
    public void SylvesterAlgorithm_Matrix_512IsHadamard() {
        TestSylvesterAlgorithm testSylvesterAlgorithm = new TestSylvesterAlgorithm();
        testSylvesterAlgorithm.createExecutorPool();

        Matrix Matrix = new Matrix(1);

        for(int i=0; i<9; i++)
            Matrix = testSylvesterAlgorithm.generateNextSizeMatrix(Matrix);

        Assert.assertTrue("Should be true.", Helpers.isIdentity(Matrix.times(Matrix.transpose())));
    }
}
