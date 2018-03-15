package hadamard;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.*;

public class SylvesterAlgorithm implements IHadamardStrategy {
    private static ThreadDataAggregator threadDataAggregator;
    protected ExecutorService executorPool;

    public void run(ThreadDataAggregator threadDataAggregator)  {
        executorPool = Executors.newFixedThreadPool(Configuration.instance.maximumNumberOfThreads);
        SylvesterAlgorithm.threadDataAggregator = threadDataAggregator;
        try {
            this.startParallelMatrixGeneration(Configuration.instance.dimension);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorPool.shutdownNow();
    }

    public boolean canExecutorForDimension(int dimension) {
        return dimension > 0 && ((dimension & (dimension - 1)) == 0);
    }

    private void startParallelMatrixGeneration(int dimension) throws InterruptedException {
        startSolving(dimension);
    }

    private Boolean startSolving(int dimension) throws InterruptedException {
        Matrix result = new Matrix(1);
        for(int i=0; i<Math.log(dimension)/Math.log(2); i++) {
            if (Configuration.instance.simulateSteps) {
                threadDataAggregator.updateMatrix(Thread.currentThread().getName(), result);
                Thread.sleep(Configuration.instance.simulationStepDelayInMS);
            }
            result = this.generateNextSizeMatrix(result);
        }

        if (Configuration.instance.printDebugMessages) {
            System.out.println("Found for dimension: " + Configuration.instance.dimension);
            System.out.println(result.getDebugStringRepresentation());
        }

        SylvesterAlgorithm.threadDataAggregator.setResult(Thread.currentThread().getName(), result);
        return true;
    }

    public Matrix generateNextSizeMatrix(Matrix source) {
        this.precheckConditions();
        Matrix resultMatrix = new Matrix(source.getDimension() * 2);

        try {
            final List<Callable<List<ConcatenatedColumn>>> partitions = new ArrayList<>();
            final int threads = Configuration.instance.maximumNumberOfThreads;

            int rangeDivisor = resultMatrix.getDimension()/threads;
            int moduloRange = resultMatrix.getDimension() % threads;
            int startValue = 0;

            if(resultMatrix.getDimension() >= threads)
                for(int i=0; i<threads; i++) {
                    final int startRange = startValue;
                    partitions.add(() -> calculateRangeColumns(startRange, rangeDivisor + startRange, source));
                    startValue += rangeDivisor;
                }

            if(startValue != resultMatrix.getDimension()) {
                final int startRange = startValue;
                partitions.add(() -> calculateRangeColumns(startRange, moduloRange + startRange, source));
            }

            final List<Future<List<ConcatenatedColumn>>> resultFromParts = executorPool.invokeAll(partitions, Configuration.instance.maxTimeOutInSeconds, TimeUnit.SECONDS);

            for (final Future<List<ConcatenatedColumn>> result : resultFromParts)
                for(ConcatenatedColumn column : result.get())
                    resultMatrix.setColumn(column.getColumn(), column.getColumnIndex());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMatrix;
    }

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

    private void precheckConditions() {
        if (threadDataAggregator.abortAllThreads.get()) {
            Thread.currentThread().interrupt();
        }
        if (Thread.currentThread().isInterrupted()){
            System.out.println(Thread.currentThread().toString() + " has been interrupted!");
            throw new CancellationException("Thread has been requested to stop");
        }
    }

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
