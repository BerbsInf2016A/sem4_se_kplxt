package hadamard;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
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

        int a = 1;
    }
}
