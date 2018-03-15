package hadamard;

import java.math.BigInteger;
import java.util.BitSet;

public class Helpers {
    /**
     * Checks if the Matrix is an identity Matrix.
     *
     * @param matrix The Matrix.
     * @return Boolean indicating if the Matrix is an identity Matrix.
     */
    public static boolean isIdentity(int[][] matrix) {
        if (matrix.length == 0)
            throw new RuntimeException("Illegal matrix dimensions.");
        if (matrix.length != matrix[0].length)
            return false;

        int identityValue = matrix[0][0];

        boolean flag = true;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++)
                if ((i == j && matrix[i][j] != identityValue) || (i != j && matrix[i][j] != 0)) {
                    flag = false;
                    break;
                }
            if (!flag)
                return false;
        }
        return true;
    }

    /**
     * Converts a BigInteger to a BitSet.
     *
     * @param bi The BigInteger value.
     * @return The resulting BitSet.
     */
    public static BitSet convertTo(BigInteger bi) {

        // TODO: Both variants seem to work. The first one seems to be faster than the second one. This needs to be tested.

        return BitSet.valueOf(reverse(bi.toByteArray()));
/*
        BitSet set = new BitSet();
        for (int i = 0; i < Configuration.instance.dimension; i++) {
            set.set(i, bi.testBit(i));
        }
        return set;
        */

    }

    private static byte[] reverse(byte[] bytes) {
        for (int i = 0; i < bytes.length / 2; i++) {
            byte temp = bytes[i];
            bytes[i] = bytes[bytes.length - i - 1];
            bytes[bytes.length - i - 1] = temp;
        }
        return bytes;
    }

    /**
     * Checks if two columns are Orthogonal to each other.
     *
     * @param firstColumn The first column.
     * @param secondColumn The second column.
     * @param targetColumnIndex The target column index.
     * @return Boolean indicating if the two columns are orthogonal.
     */
    public static boolean isOrthogonal(BitSet firstColumn, BitSet secondColumn, int targetColumnIndex) {
        if (firstColumn.length() != secondColumn.length() || firstColumn.length() < targetColumnIndex || secondColumn.length() < targetColumnIndex) {
            throw new RuntimeException("Invalid dimensions.");
        }
        int sum = 0;
        for (int i = 0; i < firstColumn.length(); i++) {
            boolean firstBit = firstColumn.get(i);
            boolean secondBit = secondColumn.get(i);
            if (firstBit != secondBit) {
                sum += -1;
            } else {
                sum++;
            }
        }
        return sum == 0;
    }

    /**
     * Concatenates two BitSets to one.
     * The two Sets need to be equal in length.
     *
     * @param set1 The first BitSet.
     * @param set2 The second BitSet.
     * @param dimension The dimension of one BitSet.
     * @return The concatenated BitSet.
     */
    public static BitSet concatenateSets(BitSet set1, BitSet set2, int dimension) {
        BitSet newSet = (BitSet) set1.clone();
        for (int i= dimension; i< 2* dimension; i++)
            newSet.set(i, set2.get(i - dimension));

        return newSet;
    }
}
