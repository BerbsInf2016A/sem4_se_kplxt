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
        try {
            final List<Callable<Boolean>> partitions = new ArrayList<>();
            final ExecutorService executorPool = Executors.newFixedThreadPool(Configuration.instance.maximumNumberOfThreads);
            BigInteger threads = BigInteger.valueOf(Configuration.instance.maximumNumberOfThreads);

            partitions.add(() -> startSolving(true, dimension));
            partitions.add(() -> startSolving(false, dimension));

            final List<Future<Boolean>> resultFromParts = executorPool.invokeAll(partitions, Configuration.instance.maxTimeOutInSeconds, TimeUnit.SECONDS);
            // Shutdown will not kill the spawned threads, but shutdownNow will set a flag which can be queried in the running
            // threads to end the execution.
            //executorPool.shutdown();
            executorPool.shutdownNow();

            for (final Future<Boolean> result : resultFromParts)
                result.get();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Boolean startSolving(boolean startValue, int dimension) {
        SylvesterMatrix result = new SylvesterMatrix(startValue);
        for(int i=0; i<Math.log(dimension)/Math.log(2); i++) {
            result = result.generateNextSizeMatrix();
        }

        Configuration.instance.debugCounter.incrementAndGet();
        if (Configuration.instance.printDebugMessages) {
            System.out.println("Found for dimension: " + Configuration.instance.dimension);
            System.out.println(result.getDebugStringRepresentation());
        }

        boolean isResultAHadamardMatrix = Helpers.isIdentity(result.times(result.transpose()));

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
    }

    public SylvesterMatrix generateNextSizeMatrix(SylvesterMatrix source) {
        SylvesterMatrix result = new SylvesterMatrix(source.getDimension() * 2);

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

    private class ConcatenatedColumn {
        BitSet column;
        int columnIndex;
        boolean needsToBeFliped;
        int dimension;

        public ConcatenatedColumn(int columnIndex, boolean needsToBeFliped, int dimension) {
            this.columnIndex = columnIndex;
            this.needsToBeFliped = needsToBeFliped;
        }

        public void generateColumn(BitSet oldMatrixColumn) {
            if(needsToBeFliped) {
                BitSet invertedColumn = (BitSet) oldMatrixColumn.clone();
                invertedColumn.flip(0, this.dimension);
                this.column = Helpers.concatenateSets(oldMatrixColumn, invertedColumn, this.dimension);
            }
        }
    }
}
