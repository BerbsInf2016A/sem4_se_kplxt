package hadamard;

import java.math.BigInteger;
import java.util.BitSet;

/**
 * Class for the Matrix. (Not the one with Keanu Reeves)
 */
public class Matrix {
    /**
     * The dimension.
     */
    private final int dimension;
    /**
     * The Columns.
     * 1 = 1 and 0 = -1
     */
    private final BitSet[] columns;
    /**
     * The Index of the next Column that is not set.
     */
    private int nextUnsetColumnIndex;

    /**
     * Constructor for the Matrix.
     *
     * @param dimension The dimension.
     */
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
        for (int i = 1; i < dimension; i++)
            this.columns[i] = new BitSet();
        this.nextUnsetColumnIndex = 1;
    }

    /**
     * Constructor for the Matrix.
     *
     * @param source The source Matrix.
     */
    public Matrix(Matrix source) {
        this.dimension = source.dimension;
        this.columns = new BitSet[this.dimension];
        for (int i = 0; i < this.dimension; i++) {
            this.columns[i] = (BitSet) source.columns[i].clone();
        }
        this.nextUnsetColumnIndex = source.nextUnsetColumnIndex;
    }

    /**
     * Gets the Dimension.
     *
     * @return The Dimension.
     */
    public int getDimension() {
        return dimension;
    }

    /**
     * Gets the Columns.
     *
     * @return The Columns.
     */
    public BitSet[] getColumns() {
        return columns;
    }

    /**
     * Sets a specific Column.
     *
     * @param column The Column.
     * @param index  The Index of the Column.
     */
    public void setColumn(BitSet column, int index) {
        this.columns[index] = column;
        if (index == this.dimension - 1) {
            this.nextUnsetColumnIndex = -1;
        } else {
            this.nextUnsetColumnIndex = index + 1;
        }
    }

    /**
     * Sets an element in the Matrix.
     *
     * @param columnIndex The Column Index.
     * @param rowIndex    The Row Index.
     * @param value       The value.
     */
    public void setElement(int columnIndex, int rowIndex, boolean value) {
        if (value)
            this.columns[columnIndex].set(rowIndex);
        else
            this.columns[columnIndex].clear(rowIndex);
    }

    /**
     * Gets the transposed Matrix.
     *
     * @return The transposed Matrix.
     */
    public Matrix transpose() {
        Matrix A = new Matrix(dimension);
        BitSet[] columns1 = this.getColumns();

        for (int columnIndex = 0; columnIndex < dimension; columnIndex++)
            for (int rowIndex = 0; rowIndex < dimension; rowIndex++)
                A.setElement(rowIndex, columnIndex, columns1[columnIndex].get(rowIndex));

        return A;
    }

    /**
     * Compares another Matrix for equality.
     *
     * @param B The Matrix to compare to.
     * @return Boolean indicating if they are equal.
     */
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

    /**
     * Multiplies the Matrix with another Matrix.
     *
     * @param B The Matrix to multiply with.
     * @return The multiplication result.
     */
    public int[][] times(Matrix B) {
        Matrix A = this;
        if (A.dimension != B.dimension) throw new RuntimeException("Illegal matrix dimensions.");
        int[][] C = new int[this.dimension][this.dimension];

        for (int rowIndex = 0; rowIndex < A.dimension; rowIndex++)
            for (int columnIndex = 0; columnIndex < A.getDimension(); columnIndex++)
                for (int index = 0; index < A.getDimension(); index++) {
                    boolean firstMatrixValue = A.getColumns()[index].get(rowIndex);
                    boolean secondMatrixValue = B.getColumns()[columnIndex].get(index);
                    C[columnIndex][rowIndex] += calculateSum(firstMatrixValue, secondMatrixValue);
                }

        return C;
    }

    /**
     * Calculates the sum of two Bit values in a Matrix.
     * 1 = 1, 0 = -1
     *
     * @param firstMatrixValue  The first value.
     * @param secondMatrixValue The second value.
     * @return The sum of the two values.
     */
    private int calculateSum(boolean firstMatrixValue, boolean secondMatrixValue) {
        if (firstMatrixValue == secondMatrixValue) {
            return 1;
        }
        return -1;
    }

    /**
     * Gets the next Column index that is not set.
     *
     * @return The next Column index that is not set.
     */
    public int getNextUnsetColumnIndex() {
        return nextUnsetColumnIndex;
    }

    /**
     * Gets the String Representation of the Matrix for the UI.
     *
     * @return The String Representation of the Matrix.
     */
    public String getUIDebugStringRepresentation() {
        String separator = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < this.dimension; row++) {
            for (int column = 0; column < this.dimension; column++) {
                if (column == 0) {
                    String value = this.columns[column].get(row) ? " 1" : "-1";
                    sb.append(value);
                } else {
                    if ((column >= nextUnsetColumnIndex && nextUnsetColumnIndex != -1)) {
                        sb.append(" ").append("-");
                        continue;
                    }
                    String value = this.columns[column].get(row) ? " 1" : "-1";
                    sb.append(" ").append(value);
                }
            }
            sb.append(separator);
        }
        return sb.toString();
    }
}
