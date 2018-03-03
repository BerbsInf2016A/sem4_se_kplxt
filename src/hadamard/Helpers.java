package hadamard;

import java.math.BigInteger;
import java.util.BitSet;

public class Helpers {
    public static boolean isIdentity(int[][] matrix){
        if(matrix.length == 0)
            throw new RuntimeException("Illegal matrix dimensions.");
        if(matrix.length != matrix[0].length)
            return false;

        int identityValue = matrix[0][0];

        /**
         * Source: http://www.penguincoders.net/2015/05/check-if-matrix-is-identity-matrix-in-java.html
         */
        boolean flag = true;
        for(int i=0;i<matrix.length;i++) {
            for(int j=0;j<matrix.length;j++)
                if((i==j && matrix[i][j] != identityValue) || (i!=j && matrix[i][j] != 0)) {
                    flag = false;
                    break;
                }
            if(!flag)
                return false;
        }
        return true;
    }
    public static BitSet convertTo (BigInteger bi) {

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
        for(int i = 0; i < bytes.length/2; i++) {
            byte temp = bytes[i];
            bytes[i] = bytes[bytes.length-i-1];
            bytes[bytes.length-i-1] = temp;
        }
        return bytes;
    }


    public static boolean isOrthogonal(BitSet firstColumn, BitSet secondColumn, int targetColumnIndex) {
        if (firstColumn.length() != secondColumn.length() || firstColumn.length() < targetColumnIndex || secondColumn.length() < targetColumnIndex){
            throw new RuntimeException("Invalid dimensions.");
        }
        int sum = 0;
        for (int i = 0; i < firstColumn.length(); i++){
            boolean firstBit = firstColumn.get(i);
            boolean secondBit = secondColumn.get(i);
            if (firstBit != secondBit)
            {
                sum += -1;
            } else{
                sum++;
            }
        }
        return sum == 0;
    }
}
