package hadamard;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.*;

public class SylvesterAlgorithm implements IHadamardStrategy {
    private static ThreadDataAggregator threadDataAggregator;

    public void run(ThreadDataAggregator threadDataAggregator){
        SylvesterAlgorithm.threadDataAggregator = threadDataAggregator;
        this.startParallelMatrixGeneration(Configuration.instance.dimension);
    }

    public boolean canExecutorForDimension(int dimension) {
        return dimension > 0 && ((dimension & (dimension - 1)) == 0);
    }

    private void startParallelMatrixGeneration(int dimension) {
        startSolving(true, dimension);
        startSolving(false, dimension);
    }

    private Boolean startSolving(boolean startValue, int dimension) {
        SylvesterMatrix result = new SylvesterMatrix(startValue);
        for(int i=0; i<Math.log(dimension)/Math.log(2); i++) {
            result = this.generateNextSizeMatrix(result);
            System.out.println("Dimension: " + result.getDimension());
        }

        Configuration.instance.debugCounter.incrementAndGet();
        if (Configuration.instance.printDebugMessages) {
            System.out.println("Found for dimension: " + Configuration.instance.dimension);
            System.out.println(result.getDebugStringRepresentation());
        }
        SylvesterAlgorithm.threadDataAggregator.setResult(Thread.currentThread().getName(), result);
        return true;

        /**Configuration.instance.debugCounter.incrementAndGet();
        if (Configuration.instance.printDebugMessages) {
            System.out.println("Found for dimension: " + Configuration.instance.dimension);
            System.out.println(result.getDebugStringRepresentation());
        }
         SylvesterAlgorithm.threadDataAggregator.setResult(Thread.currentThread().getName(), result);
        return true;
         */

        /**boolean isResultAHadamardMatrix = Helpers.isIdentity(result.times(result.transpose()));
        if (isResultAHadamardMatrix) {
            Configuration.instance.debugCounter.incrementAndGet();
            if (Configuration.instance.printDebugMessages) {
                System.out.println("Found for dimension: " + Configuration.instance.dimension);
                System.out.println(result.getDebugStringRepresentation());
            }
            SylvesterAlgorithm.threadDataAggregator.setResult(Thread.currentThread().getName(), result);
            return true;
        }

        return false;
         */
    }

    public SylvesterMatrix generateNextSizeMatrix(SylvesterMatrix source) {
        SylvesterMatrix resultMatrix = new SylvesterMatrix(source.getDimension() * 2);

        try {
            final List<Callable<List<ConcatenatedColumn>>> partitions = new ArrayList<>();
            final ExecutorService executorPool = Executors.newFixedThreadPool(Configuration.instance.maximumNumberOfThreads);
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
            // Shutdown will not kill the spawned threads, but shutdownNow will set a flag which can be queried in the running
            // threads to end the execution.
            //executorPool.shutdown();
            executorPool.shutdownNow();

            for (final Future<List<ConcatenatedColumn>> result : resultFromParts)
                for(ConcatenatedColumn column : result.get())
                    resultMatrix.setColumn(column.getColumn(), column.getColumnIndex());


        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMatrix;
    }

    private List<ConcatenatedColumn> calculateRangeColumns(int startRange, int endRange, SylvesterMatrix source) {
        List<ConcatenatedColumn> concatenatedColumns = new ArrayList<>();
        for(int i=startRange; i<endRange; i++) {
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
