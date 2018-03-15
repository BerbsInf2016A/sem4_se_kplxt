package hadamard;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.*;

/**
 * Class for the Sylvester Algorithm.
 */
public class SylvesterAlgorithmStrategy implements IHadamardStrategy {
    /**
     * The Thread Data Aggregator.
     */
    private ThreadDataAggregator threadDataAggregator;
    /**
     * The Executor Pool.
     */
    protected ExecutorService executorPool;

    /**
     * The run method for starting the algorithm.
     *
     * @param threadDataAggregator The Thread Data Aggregator.
     */
    public void run(ThreadDataAggregator threadDataAggregator)  {
        threadDataAggregator.setAlgorithmState(AlgorithmState.Running);
        executorPool = Executors.newFixedThreadPool(Configuration.instance.maximumNumberOfThreads);
        this.threadDataAggregator = threadDataAggregator;

        try {
            this.startSolving(Configuration.instance.dimension);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorPool.shutdownNow();
    }

    /**
     * Checks if the algorithm can run for a given dimension.
     *
     * @param dimension The dimension.
     * @return Boolean indicating if the Algorithm can be executed for the given dimension.
     */
    public boolean canExecuteForDimension(int dimension) {
        return dimension > 0 && ((dimension & (dimension - 1)) == 0);
    }

    /**
     * Starts generating the matrix.
     *
     * @param dimension The dimension of the matrix.
     * @throws InterruptedException Exception thrown when the thread is interrupted.
     */
    private void startSolving(int dimension) throws InterruptedException {
        Matrix result = new Matrix(1);
        for (int i=0; i<Math.log(dimension)/Math.log(2); i++) {
            if (Configuration.instance.simulateSteps) {
                threadDataAggregator.updateMatrix(Thread.currentThread().getName(), result);
                Thread.sleep(Configuration.instance.simulationStepDelayInMS);
            }
            result = this.generateNextSizeMatrix(result);
        }

        if (Configuration.instance.printDebugMessages) {
            System.out.println("Found for dimension: " + Configuration.instance.dimension);
            System.out.println(result.getUIDebugStringRepresentation());
        }

        this.threadDataAggregator.setResult(Thread.currentThread().getName(), result);
    }

    /**
     * Generates the next Hadamard matrix, double the size as the source one.
     *
     * @param source The source matrix.
     * @return The new matrix.
     */
    public Matrix generateNextSizeMatrix(Matrix source) {
        this.preCheckConditions();
        Matrix resultMatrix = new Matrix(source.getDimension() * 2);

        try {
            final List<Callable<List<IndexedColumn>>> partitions = new ArrayList<>();
            final int threads = Configuration.instance.maximumNumberOfThreads;

            // Splits the Generation of the Columns for the new Matrix into parts, so every thread can do a specific range.
            int rangeDivisor = resultMatrix.getDimension()/threads;
            // If the dimension cannot be split evenly between every thread, the reminding columns will be done separately.
            int moduloRange = resultMatrix.getDimension() % threads;
            int startValue = 0;

            // Splits up the generation between the threads.
            if (resultMatrix.getDimension() >= threads)
                for(int i=0; i<threads; i++) {
                    final int startRange = startValue;
                    partitions.add(() -> calculateRangeColumns(startRange, rangeDivisor + startRange, source));
                    startValue += rangeDivisor;
                }

            // Does the reminding columns.
            if (startValue != resultMatrix.getDimension()) {
                final int startRange = startValue;
                partitions.add(() -> calculateRangeColumns(startRange, moduloRange + startRange, source));
            }

            final List<Future<List<IndexedColumn>>> resultFromParts = executorPool.invokeAll(partitions, Configuration.instance.maxTimeOutInSeconds, TimeUnit.SECONDS);

            // Collects all the Resulting Columns and adds them to the new Matrix.
            for (final Future<List<IndexedColumn>> result : resultFromParts)
                for (IndexedColumn column : result.get())
                    resultMatrix.setColumn(column.getColumn(), column.getColumnIndex());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMatrix;
    }

    /**
     * Calculates the columns for the new matrix in a given range.
     * When the range exceeds the size of the original matrix, the values in the lower half will be inverted,
     * as it is a requirement for the Sylvester algorithm to work.
     *
     * @param startRange The start of the range.
     * @param endRange The end of the range.
     * @param source The source matrix.
     * @return List of indexed columns, consisting of the column and the index position in the new matrix.
     */
    private List<IndexedColumn> calculateRangeColumns(int startRange, int endRange, Matrix source) {
        List<IndexedColumn> indexedColumns = new ArrayList<>();
        for(int i=startRange; i<endRange; i++) {
            this.preCheckConditions();

            if(i < source.getDimension()) {
                BitSet newColumn = Helpers.concatenateSets(source.getColumns()[i], source.getColumns()[i], source.getDimension());
                indexedColumns.add(new IndexedColumn(newColumn, i));
            } else {
                BitSet invertedColumn = (BitSet) source.getColumns()[i - source.getDimension()].clone();
                invertedColumn.flip(0, source.getDimension());
                BitSet newColumn = Helpers.concatenateSets(source.getColumns()[i - source.getDimension()], invertedColumn, source.getDimension());
                indexedColumns.add(new IndexedColumn(newColumn, i));
            }
        }
        return indexedColumns;
    }

    /**
     * Checks if the abort flag is set and interrupts the current thread.
     */
    private void preCheckConditions() {
        if (threadDataAggregator.abortAllThreads.get()) {
            Thread.currentThread().interrupt();
        }
        if (Thread.currentThread().isInterrupted()) {
            System.out.println(Thread.currentThread().toString() + " has been interrupted!");
            throw new CancellationException("Thread has been requested to stop");
        }
    }

    /**
     * Class for the indexed columns.
     * It is an extension to the normal column, as it combines the newly generated columns with their index position in the new matrix.
     */
    private class IndexedColumn {
        /**
         * The column.
         */
        BitSet column;
        /**
         * The index of the column.
         */
        int columnIndex;

        /**
         *  for the IndexedColumn
         *
         * @param column The column.
         * @param columnIndex The index of the column.
         */
        public IndexedColumn(BitSet column, int columnIndex) {
            this.column = column;
            this.columnIndex = columnIndex;
        }

        /**
         * Get the column.
         *
         * @return The column.
         */
        public BitSet getColumn() {
            return column;
        }

        /**
         * Get the column index.
         *
         * @return The column index.
         */
        public int getColumnIndex() {
            return columnIndex;
        }
    }
}
