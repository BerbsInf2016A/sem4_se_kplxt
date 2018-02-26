package hadamard;

import java.math.BigInteger;
import java.util.BitSet;

public class Matrix {
    private final int dimension;
    // 1 = 1 and 0 = -1
    private final BitSet[] columns;

    public Matrix(int dimension) {
        this.dimension = dimension;
        this.columns = new BitSet[this.dimension];
        this.columns[0] = BitSet.valueOf(BigInteger.valueOf(2).pow(this.dimension)
                .subtract(BigInteger.ONE).toByteArray());
    }

    public Matrix transpose() {
        Matrix A = new Matrix(dimension);
        BitSet[] columns1 = this.columns;
        for (int i = 0; i < columns1.length; i++) {
            BitSet set = columns1[i];

            for (int index = 0; index < this.dimension; index++) {
                boolean value = set.get(index);
                for (BitSet newSet : A.columns ) {
                    if (value) {
                        newSet.set(index);
                    } else {
                        newSet.set(index, 0);
                    }
                }
            }
        }
        return A;
    }

    public boolean equals(Matrix B) {
        Matrix A = this;
        if (A.dimension != B.dimension) throw new RuntimeException("Illegal matrix dimensions.");
        BitSet[] columns1 = A.columns;
        for (int i = 0, columns1Length = columns1.length; i < columns1Length; i++) {
            BitSet aColumn = columns1[i];
            BitSet bColumn = columns1[i];

            if (!aColumn.equals(bColumn)) return false;
        }
        return true;
    }


    public int[][] times(Matrix B) {
        Matrix A = this;
        if (A.dimension != B.dimension) throw new RuntimeException("Illegal matrix dimensions.");
        int[][] C = new int[this.dimension][this.dimension];

        for (int firstMatrixRowIndex = 0; firstMatrixRowIndex < A.dimension; firstMatrixRowIndex++){
            for (int firstMatrixColumnIndex = 0; firstMatrixColumnIndex < A.dimension; firstMatrixColumnIndex++) {
                for (int secondMatrixColumnIndex = 0; secondMatrixColumnIndex < B.dimension; secondMatrixColumnIndex++){
                    for (int secondMatrixRowIndex = 0; secondMatrixRowIndex < B.dimension; secondMatrixRowIndex++){
                        boolean firstMatrixValue = A.columns[firstMatrixColumnIndex].get(firstMatrixRowIndex);
                        boolean secondMatrixValue = B.columns[secondMatrixColumnIndex].get(secondMatrixRowIndex);
                        int existingValue = C[secondMatrixColumnIndex][firstMatrixRowIndex];
                        C[secondMatrixColumnIndex][firstMatrixRowIndex] = this.calculateSum(firstMatrixValue, secondMatrixValue);
                    }
                }
            }
        }
        return C;
    }

    private int calculateSum(boolean firstMatrixValue, boolean secondMatrixValue) {
        if (firstMatrixValue == secondMatrixValue  ) {
            return 1;
        }

        return -1;
    }
}
