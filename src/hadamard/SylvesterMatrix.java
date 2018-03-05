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

    public SylvesterMatrix generateNextSizeMatrix() {
        SylvesterMatrix result = new SylvesterMatrix(this.getDimension() * 2);

        for(int i=0; i<this.getDimension(); i++) {
            BitSet newColumn = Helpers.concatenateSets(this.getColumns()[i], this.getColumns()[i], this.getDimension());
            result.setColumn(newColumn, i);
        }

        for(int i=this.getDimension(); i<result.getDimension(); i++) {
            BitSet invertedColumn = (BitSet) this.getColumns()[i - this.getDimension()].clone();
            invertedColumn.flip(0, this.getDimension());
            BitSet newColumn = Helpers.concatenateSets(this.getColumns()[i - this.getDimension()], invertedColumn, this.getDimension());
            result.setColumn(newColumn, i);
        }

        return result;
    }
}
