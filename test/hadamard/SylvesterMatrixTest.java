package hadamard;

import org.junit.Assert;
import org.junit.Test;

import java.util.BitSet;

public class SylvesterMatrixTest {
    @Test
    public void SylvesterMatrix_Two() {
        Matrix matrix = new SylvesterMatrix(1);

        Matrix secondSylvesterMatrix = new SylvesterMatrix(matrix);

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
        Matrix matrix = new SylvesterMatrix(1);

        Matrix secondSylvesterMatrix = new SylvesterMatrix(matrix);
        Matrix fourthSylvesterMatrix = new SylvesterMatrix(secondSylvesterMatrix);

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
    }

}
