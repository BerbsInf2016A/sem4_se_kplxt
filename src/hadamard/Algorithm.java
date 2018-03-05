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
    public void run(){
        Matrix startMatrix = this.generateStartMatrix(Configuration.instance.dimension);
        this.startParallelSearch(Configuration.instance.dimension, startMatrix);
    }

    private void startParallelSearch(int dimension, Matrix startMatrix) {
        try {
            final List<Callable<Boolean>> partitions = new ArrayList<>();
            final ExecutorService executorPool = Executors.newFixedThreadPool(Configuration.instance.maximumNumberOfThreads);

            BigInteger threads = BigInteger.valueOf(Configuration.instance.maximumNumberOfThreads);

            BigInteger maxValue = BigInteger.valueOf(2).pow(dimension - 1);
            BigInteger sliceSize = maxValue.divide(threads);

            for (BigInteger i = BigInteger.ZERO; i.compareTo(maxValue) != 1; i = i.add(sliceSize)) {
                final BigInteger from = i;
                BigInteger to = i.add(sliceSize);
                if (to.compareTo(maxValue) == 1)
                    to = maxValue;
                final BigInteger end = to;
                partitions.add(() -> startSolving(startMatrix, from, end));
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

    private Boolean startSolving(Matrix startMatrix, BigInteger from, BigInteger end) {
        if (Configuration.instance.printDebugMessages) {
            System.out.println("Searching in the range from " + from + " to " + end);
        }
        for (BigInteger i = from; i.compareTo(end) != 1; i = i.add(BigInteger.ONE)) {
            BitSet combination = Helpers.convertTo(i);
            combination.set(startMatrix.getDimension() - 1);
            if ((combination.cardinality()) == (startMatrix.getDimension() / 2)){
                Matrix newMatrix = new Matrix(startMatrix);
                if (!this.checkOrthogonalityWithExistingColumns(newMatrix.getColumns(), combination, 1)) {
                    continue;
                }
                newMatrix.setColumn(combination, 1);
                if (this.solve(newMatrix)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean solve(Matrix matrix) {
        int nextColumnIndex = matrix.getNextUnsetColumnIndex();
        if (nextColumnIndex == - 1){
            // Validate
            Matrix transpose = matrix.transpose();
            int[][] result = matrix.times(transpose);
            if (Helpers.isIdentity(result)){
                Configuration.instance.debugCounter.incrementAndGet();
                if (Configuration.instance.printDebugMessages) {
                    System.out.println("Found for dimension: " + Configuration.instance.dimension);
                    System.out.println(matrix.getDebugStringRepresentation());
                }
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

    private Matrix generateStartMatrix(int dimension) {
        return new Matrix(dimension);
    }
}
