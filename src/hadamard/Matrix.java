package hadamard;

import java.math.BigInteger;
import java.util.BitSet;

public class Matrix {
    private final int dimension;
    // 1 = 1 and 0 = -1
    private final BitSet[] columns;
    private int nextUnsetColumnIndex;

    public Matrix(int dimension) {
        this.dimension = dimension;
        this.columns = new BitSet[this.dimension];
        // Attention: It is possible to get a byte[] for a BigInteger and to create
        // a BitSet from a byte[]. But one uses little Endian and one uses big endian.
        // So we cannot use it.
        // See https://docs.oracle.com/javase/7/docs/api/java/math/BigInteger.html#toByteArray() --> Big endian
        // https://docs.oracle.com/javase/7/docs/api/java/util/BitSet.html#valueOf(byte[]) --> Little endian
        BigInteger bigintValue = BigInteger.valueOf(2).pow(this.dimension)
                .subtract(BigInteger.ONE);
        this.columns[0] = Helpers.convertTo(bigintValue);
        for(int i = 1; i < dimension; i++)
            this.columns[i] = new BitSet();
    }

    public Matrix(Matrix source) {
        this.dimension = source.dimension;
        this.columns = new BitSet[this.dimension];
        for(int i = 0; i < this.dimension; i++){
            this.columns[i] = (BitSet) source.columns[i].clone();
        }
    }

    public int getDimension() {
        return dimension;
    }

    public BitSet[] getColumns() {
        return columns;
    }

    public void setColumn(BitSet column, int index) {
        this.columns[index] = column;
        if (index == this.dimension - 1) {
            this.nextUnsetColumnIndex = -1;
        } else {
            this.nextUnsetColumnIndex = index + 1;
        }
    }

    public void setElement(int columnIndex, int rowIndex, boolean value) {
        if(value)
            this.columns[columnIndex].set(rowIndex);
        else
            this.columns[columnIndex].clear(rowIndex);
    }

    public Matrix transpose() {
        Matrix A = new Matrix(dimension);
        BitSet[] columns1 = this.getColumns();

        for (int columnIndex = 0; columnIndex < dimension; columnIndex++)
            for (int rowIndex = 0; rowIndex < dimension; rowIndex++)
                A.setElement(rowIndex, columnIndex, columns1[columnIndex].get(rowIndex));

        return A;
    }

    public boolean equals(Matrix B) {
        Matrix A = this;
        if (A.getDimension() != B.getDimension()) throw new RuntimeException("Illegal matrix dimensions.");
        BitSet[] columns1 = A.getColumns();
        BitSet[] columns2 = B.getColumns();
        for (int i = 0, columns1Length = columns1.length; i < columns1Length; i++) {
            BitSet aColumn = columns1[i];
            BitSet bColumn = columns2[i];

            if (!aColumn.equals(bColumn)) return false;
        }
        return true;
    }


    public int[][] times(Matrix B) {
        Matrix A = this;
        if (A.dimension != B.dimension) throw new RuntimeException("Illegal matrix dimensions.");
        int[][] C = new int[this.dimension][this.dimension];

        for (int rowIndex = 0; rowIndex < A.dimension; rowIndex++)
            for (int columnIndex = 0; columnIndex < A.getDimension(); columnIndex++)
                for (int index = 0; index < A.getDimension(); index++){
                        boolean firstMatrixValue = A.getColumns()[index].get(rowIndex);
                        boolean secondMatrixValue = B.getColumns()[columnIndex].get(index);
                        C[columnIndex][rowIndex] += calculateSum(firstMatrixValue, secondMatrixValue);
                }
                
        return C;
    }

    private int calculateSum(boolean firstMatrixValue, boolean secondMatrixValue) {
        if (firstMatrixValue == secondMatrixValue  ) {
            return 1;
        }
        return -1;
    }

    public int getNextUnsetColumnIndex() {
        return nextUnsetColumnIndex;
    }

    public String getDebugStringRepresentation(){
        String separator = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < Configuration.instance.dimension; row++){
            for (int column = 0; column < Configuration.instance.dimension; column++) {
                if (column == 0) {
                    String value = this.columns[column].get(row) ? " 1" : "-1";
                    sb.append(value);
                } else {
                    String value = this.columns[column].get(row) ? " 1" : "-1";
                    sb.append(" ").append(value);
                }
            }
            sb.append(separator);
        }
        return sb.toString();
    }
}
