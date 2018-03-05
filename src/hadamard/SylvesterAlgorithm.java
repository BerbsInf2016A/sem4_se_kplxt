package hadamard;

import java.util.BitSet;

public class SylvesterAlgorithm implements IHadamardStrategy {
    public void run(ThreadDataAggregator threadDataAggregator) {

    }

    private void startParallelMatrixGeneration(int dimension, Matrix startMatrix) {

    }

    private Matrix generateNextSizeMatrix(Matrix source) {
        Matrix result = new Matrix(source.getDimension() * 2);

        for(int i=0; i<source.getDimension(); i++) {
            BitSet newColumn = Helpers.concatenateSets(source.getColumns()[i], source.getColumns()[i], source.getDimension());
            result.setColumn(newColumn, i);
        }

        for(int i=source.getDimension(); i<result.getDimension(); i++) {
            BitSet invertedColumn = (BitSet) source.getColumns()[i - source.getDimension()].clone();
            invertedColumn.flip(0, source.getDimension());
            BitSet newColumn = Helpers.concatenateSets(source.getColumns()[i - source.getDimension()], invertedColumn, source.getDimension());
            result.setColumn(newColumn, i);
        }

        return result;
    }
}
