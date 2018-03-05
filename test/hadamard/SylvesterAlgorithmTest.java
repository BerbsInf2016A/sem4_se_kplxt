package hadamard;

import org.junit.Assert;
import org.junit.Test;

import java.util.BitSet;

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

    @Test
    public void SylvesterMatrix_Two() {
        SylvesterAlgorithm sylvesterAlgorithm = new SylvesterAlgorithm();
        SylvesterMatrix sylvesterMatrix = new SylvesterMatrix(true);

        SylvesterMatrix secondSylvesterMatrix = sylvesterAlgorithm.generateNextSizeMatrix(sylvesterMatrix);

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
        SylvesterAlgorithm sylvesterAlgorithm = new SylvesterAlgorithm();
        SylvesterMatrix sylvesterMatrix = new SylvesterMatrix(true);

        SylvesterMatrix secondSylvesterMatrix = sylvesterAlgorithm.generateNextSizeMatrix(sylvesterMatrix);
        SylvesterMatrix fourthSylvesterMatrix = sylvesterAlgorithm.generateNextSizeMatrix(secondSylvesterMatrix);

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
        SylvesterAlgorithm sylvesterAlgorithm = new SylvesterAlgorithm();
        SylvesterMatrix sylvesterMatrix = new SylvesterMatrix(true);

        for(int i=0; i<3; i++)
            sylvesterMatrix = sylvesterAlgorithm.generateNextSizeMatrix(sylvesterMatrix);

        Assert.assertTrue("Should be true.", Helpers.isIdentity(sylvesterMatrix.times(sylvesterMatrix.transpose())));
    }

    @Test
    public void SylvesterMatrix_EightIsHadamardWithStartValueMinus1() {
        SylvesterAlgorithm sylvesterAlgorithm = new SylvesterAlgorithm();
        SylvesterMatrix sylvesterMatrix = new SylvesterMatrix(false);

        for(int i=0; i<3; i++)
            sylvesterMatrix = sylvesterAlgorithm.generateNextSizeMatrix(sylvesterMatrix);

        Assert.assertTrue("Should be true.", Helpers.isIdentity(sylvesterMatrix.times(sylvesterMatrix.transpose())));
    }

    @Test
    public void SylvesterMatrix_SixteenIsHadamardWithStartValue1() {
        SylvesterAlgorithm sylvesterAlgorithm = new SylvesterAlgorithm();
        SylvesterMatrix sylvesterMatrix = new SylvesterMatrix(true);

        for(int i=0; i<4; i++)
            sylvesterMatrix = sylvesterAlgorithm.generateNextSizeMatrix(sylvesterMatrix);

        Assert.assertTrue("Should be true.", Helpers.isIdentity(sylvesterMatrix.times(sylvesterMatrix.transpose())));
    }

    @Test
    public void SylvesterMatrix_512IsHadamardWithStartValueMinus1() {
        SylvesterAlgorithm sylvesterAlgorithm = new SylvesterAlgorithm();
        SylvesterMatrix sylvesterMatrix = new SylvesterMatrix(true);

        for(int i=0; i<9; i++)
            sylvesterMatrix = sylvesterAlgorithm.generateNextSizeMatrix(sylvesterMatrix);

        Assert.assertTrue("Should be true.", Helpers.isIdentity(sylvesterMatrix.times(sylvesterMatrix.transpose())));
    }
}
