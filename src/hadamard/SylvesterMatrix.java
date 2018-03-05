package hadamard;

import java.util.BitSet;

public class SylvesterMatrix extends Matrix {
    public SylvesterMatrix(boolean startValue) {
        super(1);
        BitSet column = new BitSet();
        column.set(0, startValue);
        this.setColumn(column, 0);
    }

    public SylvesterMatrix(int dimension) {
        super(dimension);
    }
}
