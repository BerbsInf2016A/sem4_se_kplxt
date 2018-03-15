package hadamard;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.*;

/**
 * Class for the Sylvester Algorithm.
 */
public class SylvesterAlgorithm implements IHadamardStrategy {
    /**
     * The Thread Data Aggregator.
     */
    private static ThreadDataAggregator threadDataAggregator;
    /**
     * The Executor Pool.
     */
    protected ExecutorService executorPool;

    /**
     * The run Method for starting the Algorithm.
     *
     * @param threadDataAggregator The Thread Data Aggregator.
     */
    public void run(ThreadDataAggregator threadDataAggregator)  {
        threadDataAggregator.setApplicationState(AlgorithmState.Running);
        executorPool = Executors.newFixedThreadPool(Configuration.instance.maximumNumberOfThreads);
        SylvesterAlgorithm.threadDataAggregator = threadDataAggregator;

        try {
            this.startSolving(Configuration.instance.dimension);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorPool.shutdownNow();
    }

    /**
     * Checks if the Algorithm can run for a given dimension.
     *
     * @param dimension The dimension.
     * @return Boolean indicating if the Algorithm can be executed for the given dimension.
     */
    public boolean canExecutorForDimension(int dimension) {
        return dimension > 0 && ((dimension & (dimension - 1)) == 0);
    }

    /**
     * Starts generating the Matrix.
     *
     * @param dimension The dimension of the Matrix.
     * @throws InterruptedException Exception thrown when the Task is Interrupted.
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

        SylvesterAlgorithm.threadDataAggregator.setResult(Thread.currentThread().getName(), result);
    }

    /**
     * Generates the next Hadamard Matrix, double the size as the source one.
     *
     * @param source The source Matrix.
     * @return The new Matrix.
     */
    public Matrix generateNextSizeMatrix(Matrix source) {
        this.precheckConditions();
        Matrix resultMatrix = new Matrix(source.getDimension() * 2);

        try {
            final List<Callable<List<ConcatenatedColumn>>> partitions = new ArrayList<>();
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

            final List<Future<List<ConcatenatedColumn>>> resultFromParts = executorPool.invokeAll(partitions, Configuration.instance.maxTimeOutInSeconds, TimeUnit.SECONDS);

            // Collects all the Resulting Columns and adds them to the new Matrix.
            for (final Future<List<ConcatenatedColumn>> result : resultFromParts)
                for (ConcatenatedColumn column : result.get())
                    resultMatrix.setColumn(column.getColumn(), column.getColumnIndex());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMatrix;
    }

    /**
     * Calculates the columns for the new Matrix in a given range.
     * When the range exceeds the size of the original Matrix, the Values in the lower half will be inverted.
     * As it is a requirement for the Sylvester Algorithm to work.
     *
     * @param startRange The start of the range.
     * @param endRange The end of the range.
     * @param source The source Matrix.
     * @return List of Concatenated Columns, consisting of the Column and the index position in the new Matrix.
     */
    private List<ConcatenatedColumn> calculateRangeColumns(int startRange, int endRange, Matrix source) {
        List<ConcatenatedColumn> concatenatedColumns = new ArrayList<>();
        for(int i=startRange; i<endRange; i++) {
            this.precheckConditions();

            if(i < source.getDimension()) {
                BitSet newColumn = Helpers.concatenateSets(source.getColumns()[i], source.getColumns()[i], source.getDimension());
                concatenatedColumns.add(new ConcatenatedColumn(newColumn, i));
            } else {
                BitSet invertedColumn = (BitSet) source.getColumns()[i - source.getDimension()].clone();
                invertedColumn.flip(0, source.getDimension());
                BitSet newColumn = Helpers.concatenateSets(source.getColumns()[i - source.getDimension()], invertedColumn, source.getDimension());
                concatenatedColumns.add(new ConcatenatedColumn(newColumn, i));
            }
        }
        return concatenatedColumns;
    }

    /**
     * Checks if the abort flag is set and interrupts the current Thread.
     */
    private void precheckConditions() {
        if (threadDataAggregator.abortAllThreads.get()) {
            Thread.currentThread().interrupt();
        }
        if (Thread.currentThread().isInterrupted()) {
            System.out.println(Thread.currentThread().toString() + " has been interrupted!");
            throw new CancellationException("Thread has been requested to stop");
        }
    }

    /**
     * Class for the Concatenated Columns.
     * Is an extension to the normal Column, as it combines the newly generated Columns with their index position in the new Matrix.
     */
    private class ConcatenatedColumn {
        BitSet column;
        int columnIndex;

        public ConcatenatedColumn(BitSet column, int columnIndex) {
            this.column = column;
            this.columnIndex = columnIndex;
        }

        public BitSet getColumn() {
            return column;
        }

        public int getColumnIndex() {
            return columnIndex;
        }
    }
}
