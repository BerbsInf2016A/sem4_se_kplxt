package hadamard;

import java.util.BitSet;

public interface IMatrixChangedListener {
    void matrixChanged(String threadName, Matrix changedMatrix);
    void matrixColumnChanged(String threadName, int columnIndex, BitSet column);
}
