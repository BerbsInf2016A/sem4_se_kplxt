package hadamard;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;

public class MatrixTest {

    @Test
    public void MatrixTest_Transpose() {
        Matrix testMatrix = new Matrix(4);
        testMatrix.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1111", 2)).toByteArray()), 0);
        testMatrix.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1100", 2)).toByteArray()), 1);
        testMatrix.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1110", 2)).toByteArray()), 2);
        testMatrix.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1011", 2)).toByteArray()), 3);

        Matrix expectedResult = new Matrix(4);
        expectedResult.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1001", 2)).toByteArray()), 0);
        expectedResult.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1101", 2)).toByteArray()), 1);
        expectedResult.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("0111", 2)).toByteArray()), 2);
        expectedResult.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1111", 2)).toByteArray()), 3);

        Matrix transposedMatrix = testMatrix.transpose();

        Assert.assertTrue("Should be true.", transposedMatrix.equals(expectedResult));
    }

    @Test
    public void MatrixTest_Equals() {
        Matrix testMatrix = new Matrix(4);
        testMatrix.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1001", 2)).toByteArray()), 0);
        testMatrix.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("0000", 2)).toByteArray()), 1);
        testMatrix.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1111", 2)).toByteArray()), 2);
        testMatrix.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1011", 2)).toByteArray()), 3);

        Matrix expectedResult = new Matrix(4);
        expectedResult.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1001", 2)).toByteArray()), 0);
        expectedResult.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("0000", 2)).toByteArray()), 1);
        expectedResult.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1111", 2)).toByteArray()), 2);
        expectedResult.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1011", 2)).toByteArray()), 3);

        Assert.assertTrue("Should be true.", testMatrix.equals(expectedResult));
    }

    @Test
    public void MatrixTest_Times() {
        Matrix testMatrix1 = new Matrix(4);
        testMatrix1.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("0101", 2)).toByteArray()), 0);
        testMatrix1.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("0001", 2)).toByteArray()), 1);
        testMatrix1.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1010", 2)).toByteArray()), 2);
        testMatrix1.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("0101", 2)).toByteArray()), 3);

        Matrix testMatrix2 = new Matrix(4);
        testMatrix2.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1001", 2)).toByteArray()), 0);
        testMatrix2.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("0000", 2)).toByteArray()), 1);
        testMatrix2.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1111", 2)).toByteArray()), 2);
        testMatrix2.setColumn(BitSet.valueOf(BigInteger.valueOf(Integer.parseInt("1011", 2)).toByteArray()), 3);

        int[][] result = testMatrix1.times(testMatrix2);

        int expectedResult[][] = {{2,-2,4,-2}, {-2,2,0,2}, {2, -2, 0, -2}, {4, -4, 2, -4}};

        Assert.assertTrue("Should be true.", Arrays.deepEquals(expectedResult, result));
    }

    @Test
    public void MatrixTest_IsIdentityTrue() {
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

        Matrix testMatrix = new Matrix(4);
        testMatrix.setColumn(firstColumn, 0);
        testMatrix.setColumn(secondColumn, 1);
        testMatrix.setColumn(thirdColumn, 2);
        testMatrix.setColumn(fourthColumn, 3);

        Assert.assertTrue("Should be true.", testMatrix.isIdentity());
    }


    @Test
    public void MatrixTest_IsIdentityFalse() {
        BitSet firstColumn = new BitSet();
        firstColumn.set(0);
        firstColumn.set(1);
        firstColumn.set(2);
        firstColumn.clear(3);

        BitSet secondColumn = new BitSet();
        secondColumn.set(0);
        secondColumn.clear(1);
        secondColumn.set(2);
        secondColumn.clear(3);

        BitSet thirdColumn = new BitSet();
        thirdColumn.set(0);
        thirdColumn.clear(1);
        thirdColumn.clear(2);
        thirdColumn.set(3);

        BitSet fourthColumn = new BitSet();
        fourthColumn.set(0);
        fourthColumn.clear(1);
        fourthColumn.clear(2);
        fourthColumn.set(3);

        Matrix testMatrix = new Matrix(4);
        testMatrix.setColumn(firstColumn, 0);
        testMatrix.setColumn(secondColumn, 1);
        testMatrix.setColumn(thirdColumn, 2);
        testMatrix.setColumn(fourthColumn, 3);

        Assert.assertFalse("Should be false.", testMatrix.isIdentity());
    }
}
