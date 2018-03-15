package hadamard;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.*;

/**
 * A backtracking algorithm to find Hadamard matrices.
 */
public class BacktrackingAlgorithmStrategy implements IHadamardStrategy {
    /**
     * The ThreadDataAggregator which is used, to aggregate the information from the different threads
     * to view them in the UI.
     */
    private ThreadDataAggregator threadDataAggregator;

    /**
     * Execute the strategy.
     *
     * @param threadDataAggregator The Thread Data Aggregator.
     */
    @Override
    public void run(ThreadDataAggregator threadDataAggregator) {
        this.threadDataAggregator = threadDataAggregator;
        Matrix startMatrix = new Matrix(Configuration.instance.dimension);
        this.startParallelSearch(Configuration.instance.dimension, startMatrix);
    }

    /**
     * Checks if this algorithm can run for a given dimension.
     *
     * @param dimension The dimension.
     * @return True if this algorithm can be executed for the dimension, false if not.
     */
    @Override
    public boolean canExecuteForDimension(int dimension) {
        return dimension > 0 && (dimension == 1 || dimension == 2 || dimension % 4 == 0);
    }

    /**
     * Start the search with multiple threads.
     *
     * @param dimension The dimension to search for.
     * @param startMatrix The start matrix.
     */
    private void startParallelSearch(int dimension, Matrix startMatrix) {
        threadDataAggregator.setAlgorithmState(AlgorithmState.Running);
        if (dimension == 1) {
            threadDataAggregator.setResult(Thread.currentThread().getName(), new Matrix(dimension));
            threadDataAggregator.setAlgorithmState(AlgorithmState.ResultFound);
            return;
        }
        // Partition for a parallel search,
        try {
            final List<Callable<Boolean>> partitions = new ArrayList<>();
            final ExecutorService executorPool = Executors.newFixedThreadPool(Configuration.instance.maximumNumberOfThreads);

            BigInteger threads = BigInteger.valueOf(Configuration.instance.maximumNumberOfThreads);

            BigInteger maxValue = BigInteger.valueOf(2).pow(dimension - 1);
            BigInteger sliceSize = maxValue.divide(threads);

            // SliceSize smaller or equal to the number of available threads.
            if (sliceSize.compareTo(threads) != 1) {
                for (BigInteger i = BigInteger.ZERO; i.compareTo(maxValue) != 1; i = i.add(BigInteger.ONE)) {
                    final BigInteger from = i;
                    final BigInteger end = i;
                    partitions.add(() -> startSolving(startMatrix, from, end));
                }
            } else { // SliceSize bigger than the available number of threads.
                for (BigInteger i = BigInteger.ZERO; i.compareTo(maxValue) != 1; i = i.add(sliceSize)) {
                    final BigInteger from = i;
                    BigInteger to = i.add(sliceSize);
                    if (to.compareTo(maxValue) == 1)
                        to = maxValue;
                    final BigInteger end = to;
                    partitions.add(() -> startSolving(startMatrix, from, end));
                }
            }


            final List<Future<Boolean>> resultFromParts = executorPool.invokeAll(partitions, Configuration.instance.maxTimeOutInSeconds, TimeUnit.SECONDS);
            // Shutdown will not kill the spawned threads, but shutdownNow will set a flag which can be queried in the running
            // threads to end the execution.
            //executorPool.shutdown();
            executorPool.shutdownNow();

            for (final Future<Boolean> result : resultFromParts)
                result.get();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Start the solving for a given matrix in a given range.
     *
     * @param startMatrix The matrix to start the search on.
     * @param from The lower limit of the search range.
     * @param end The upper limit of the search range.
     * @return True if a result was found, false if not.
     * @throws InterruptedException Can be thrown if the thread is interrupted.
     */
    private Boolean startSolving(Matrix startMatrix, BigInteger from, BigInteger end) throws InterruptedException {
        this.preCheckConditions();
        if (Configuration.instance.printDebugMessages)
            System.out.println("Searching in the range from " + from + " to " + end);

        if (Configuration.instance.simulateSteps)
            this.threadDataAggregator.updateMatrix(Thread.currentThread().getName(), startMatrix);

        // Iterate all possible columns in the range.
        for (BigInteger i = from; i.compareTo(end) != 1; i = i.add(BigInteger.ONE)) {
            // Get the bit combination for this round.
            BitSet combination = Helpers.convertTo(i);
            combination.set(startMatrix.getDimension() - 1);
            this.simulateStep(Thread.currentThread().getName(), startMatrix, combination, 1);

            // Check if this is a valid column value.
            if ((combination.cardinality()) == (startMatrix.getDimension() / 2)) {
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

    /**
     * A recursive method which tries to generate the next needed column for a given matrix.
     *
     * @param sourceMatrix The matrix to work on.
     * @return True if a result has been found, false if not.
     * @throws InterruptedException Can be thrown if the thread is interrupted.
     */
    private boolean solve(Matrix sourceMatrix) throws InterruptedException {
        this.preCheckConditions();
        int nextColumnIndex = sourceMatrix.getNextUnsetColumnIndex();
        // Handle possible result.
        if (nextColumnIndex == -1) {
            threadDataAggregator.setAlgorithmState(AlgorithmState.Validating);
            // Validate
            Matrix transpose = sourceMatrix.transpose();
            int[][] result = sourceMatrix.times(transpose);
            if (Helpers.isIdentity(result)) {
                if (Configuration.instance.printDebugMessages) {
                    System.out.println("Found for dimension: " + Configuration.instance.dimension);
                    System.out.println(sourceMatrix.getUIDebugStringRepresentation());
                }
                threadDataAggregator.setResult(Thread.currentThread().getName(), sourceMatrix);
                return true;
            }
        } else {
            // Iterate all possible bit combinations for this column.
            for (BigInteger i = BigInteger.ZERO; i.compareTo(BigInteger.valueOf(2).pow(sourceMatrix.getDimension() - 1)) < 0; i = i.add(BigInteger.ONE)) {
                this.preCheckConditions();
                BitSet combination = Helpers.convertTo(i);
                combination.set(sourceMatrix.getDimension() - 1);
                Matrix newMatrix = new Matrix(sourceMatrix);
                this.simulateStep(Thread.currentThread().getName(), newMatrix, combination, nextColumnIndex);

                // Check if this is a valid column value.
                if ((combination.cardinality()) == (sourceMatrix.getDimension() / 2)) {
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

    /**
     * Simulate a step for the UI.
     *
     * @param name The name of the thread.
     * @param newMatrix The new matrix to show on the ui.
     * @param column The new column to set.
     * @param nextColumnIndex The index of the column to set.
     * @throws InterruptedException Can be thrown if the thread is interrupted.
     */
    private void simulateStep(String name, Matrix newMatrix, BitSet column, int nextColumnIndex) throws InterruptedException {
        if (Configuration.instance.simulateSteps) {
            Thread.sleep(Configuration.instance.simulationStepDelayInMS);
            // Copy the matrix to avoid any altering on the original one.
            Matrix reportMatrix = new Matrix(newMatrix);
            reportMatrix.setColumn(column, nextColumnIndex);
            this.threadDataAggregator.updateMatrixColumn(name, nextColumnIndex, column);
        }
    }

    /**
     * Check if the execution can continue. Aborts the execution if necessary.
     */
    private void preCheckConditions() {
        if (threadDataAggregator.abortAllThreads.get())
            Thread.currentThread().interrupt();

        if (Thread.currentThread().isInterrupted()) {
            System.out.println(Thread.currentThread().getName() + " has been interrupted!");
            throw new CancellationException("Thread has been requested to stop");
        }
        if (threadDataAggregator.threadAlreadyFoundResult(Thread.currentThread().getName())) {
            Thread.currentThread().interrupt();
            throw new CancellationException("Thread has been requested to stop, because it already found a result");
        }
    }

    /**
     * Check if a column is orthogonal to all existing columns.
     *
     * @param columns The existing columns.
     * @param newColumn The new column to check against the existing columns.
     * @param targetColumnIndex The index for the new column.
     * @return True of the new column is orthogonal to all existing columns, false if not.
     */
    private boolean checkOrthogonalityWithExistingColumns(BitSet[] columns, BitSet newColumn, int targetColumnIndex) {
        for (int i = 0; i < targetColumnIndex; i++) {
            BitSet column = columns[i];
            if (!Helpers.isOrthogonal(column, newColumn, targetColumnIndex)) return false;
        }
        return true;
    }
}
