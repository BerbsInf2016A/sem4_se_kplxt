package hadamard;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

// TODO: Change all classes to use the dimension in the configuration.

public class Algorithm {
    public void run(int dimension){
        Configuration.instance.dimension = dimension;
        Matrix startMatrix = this.generateStartMatrix(dimension);
        List<Matrix> firstFieldCombinations = this.generateCombinations(startMatrix, 1);
        this.startParallelSearch(firstFieldCombinations);
    }
    // TODO Split start into number ranges instead of predefined matrices. This will be better for bigger dimensions. Reasons for the change: Removing duplicate code and the current version will not work with big dimensions.

    private void startParallelSearch(List<Matrix> firstFieldCombinations) {
        try {
        final List<Callable<Boolean>> partitions = new ArrayList<>();
        final ExecutorService executorPool = Executors.newFixedThreadPool(Configuration.instance.maximumNumberOfThreads);

        int sliceSize = firstFieldCombinations.size() / Configuration.instance.maximumNumberOfThreads;
        if (firstFieldCombinations.size() < Configuration.instance.maximumNumberOfThreads) {
            for (Matrix matrix : firstFieldCombinations ) {
                partitions.add(() -> startSolving(Arrays.asList(matrix) ));
            }
        } else {
            for (int i = 0; i <= firstFieldCombinations.size(); i += sliceSize) {
                int to = i + sliceSize;
                if (to > firstFieldCombinations.size())
                    to = firstFieldCombinations.size();
                final int end = to;
                List<Matrix> sublist = firstFieldCombinations.subList(i, end);
                partitions.add(() -> startSolving(sublist));
            }
        }


        final List<Future<Boolean>> resultFromParts = executorPool.invokeAll(partitions, Configuration.instance.maxTimeOutInSeconds, TimeUnit.SECONDS);
        // Shutdown will not kill the spawned threads, but shutdownNow will set a flag which can be queried in the running
        // threads to end the execution.
        //executorPool.shutdown();
        executorPool.shutdownNow();

        for (final Future<Boolean> result : resultFromParts)
            result.get();

        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Boolean startSolving(List<Matrix> sublist) {
        for (Matrix matrix : sublist ) {
            solve(matrix);
        }
        return true;
    }

    private boolean solve(Matrix matrix) {
        int nextColumnIndex = matrix.getNextUnsetColumnIndex();
        if (nextColumnIndex == - 1){
            // Validate
            Matrix transpose = matrix.transpose();
            int[][] result = matrix.times(transpose);
            if (Helpers.isIdentity(result)){
                Configuration.instance.debugCounter.incrementAndGet();
                System.out.println("Found for dimension: " + Configuration.instance.dimension);
                System.out.println(matrix.getDebugStringRepresentation());
                return true;
            }
        } else {
            for (BigInteger i = BigInteger.ZERO; i.compareTo( BigInteger.valueOf(2).pow(matrix.getDimension() - 1)) < 0; i = i.add(BigInteger.ONE)) {
                BitSet combination = Helpers.convertTo(i);
                combination.set(matrix.getDimension() - 1);
                if ((combination.cardinality()) == (matrix.getDimension() / 2)){
                    Matrix newMatrix = new Matrix(matrix);
                    if (!this.checkOrthogonalityWithExistingColumns(newMatrix.getColumns(), combination, nextColumnIndex)) {
                        continue;
                    }
                    newMatrix.setColumn(combination, nextColumnIndex);
                    if (this.solve(newMatrix)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkOrthogonalityWithExistingColumns(BitSet[] columns, BitSet combination, int targetColumnIndex) {
        for (int i = 0; i < targetColumnIndex; i++) {
            BitSet column = columns[i];
            if (!Helpers.isOrthogonal(column, combination, targetColumnIndex)) return false;
        }
        return true;
    }

    private List<Matrix> generateCombinations(Matrix startMatrix, int targetColumnIndex) {
        ArrayList<Matrix> matrices = new ArrayList<>();
        for (BigInteger i = BigInteger.ZERO; i.compareTo( BigInteger.valueOf(2).pow(startMatrix.getDimension() - 1)) < 0; i = i.add(BigInteger.ONE)) {
            BitSet combination = Helpers.convertTo(i);
            if ((combination.cardinality() + 1) == (startMatrix.getDimension() / 2)){
                Matrix newMatrix = new Matrix(startMatrix);
                combination.set(startMatrix.getDimension() - 1);
                newMatrix.setColumn(combination, targetColumnIndex);
                matrices.add(newMatrix);
            }
        }
        return matrices;
    }

    private Matrix generateStartMatrix(int dimension) {
        return new Matrix(dimension);
    }
}
