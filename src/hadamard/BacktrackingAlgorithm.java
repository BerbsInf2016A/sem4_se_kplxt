package hadamard;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.*;

import static javafx.scene.input.KeyCode.T;


// TODO: Change all classes to use the dimension in the configuration.

public class BacktrackingAlgorithm implements IHadamardStrategy {
    private static ThreadDataAggregator threadDataAggreagtor;

    public void run(ThreadDataAggregator threadDataAggregator){
        this.threadDataAggreagtor = threadDataAggregator;
        Matrix startMatrix = this.generateStartMatrix(Configuration.instance.dimension);
        this.startParallelSearch(Configuration.instance.dimension, startMatrix);
    }

    @Override
    public boolean canExecutorForDimension(int dimension) {
        if (dimension > 0 )
            return true;
        return false;
    }

    private void startParallelSearch(int dimension, Matrix startMatrix) {
        try {
            final List<Callable<Boolean>> partitions = new ArrayList<>();
            final ExecutorService executorPool = Executors.newFixedThreadPool(Configuration.instance.maximumNumberOfThreads);

            BigInteger threads = BigInteger.valueOf(Configuration.instance.maximumNumberOfThreads);

            BigInteger maxValue = BigInteger.valueOf(2).pow(dimension - 1);
            BigInteger sliceSize = maxValue.divide(threads);

            if (sliceSize.compareTo(threads) != 1){
                for(BigInteger i = BigInteger.ZERO; i.compareTo(maxValue) != 1; i = i.add(BigInteger.ONE)){
                    final BigInteger from = i;
                    final BigInteger end = i;
                    partitions.add(() -> startSolving(startMatrix, from, end));
                }
            } else {
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

        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Boolean startSolving(Matrix startMatrix, BigInteger from, BigInteger end) throws InterruptedException {
        this.precheckConditions();
        if (Configuration.instance.printDebugMessages) {
            System.out.println("Searching in the range from " + from + " to " + end);
        }
        if (Configuration.instance.simulateSteps) {
            BacktrackingAlgorithm.threadDataAggreagtor.updateMatrix(Thread.currentThread().getName(), startMatrix);
        }
        for (BigInteger i = from; i.compareTo(end) != 1; i = i.add(BigInteger.ONE)) {
            BitSet combination = Helpers.convertTo(i);
            combination.set(startMatrix.getDimension() - 1);
            this.simulateStep(Thread.currentThread().getName(), startMatrix, combination, 1 );
            if ((combination.cardinality()) == (startMatrix.getDimension() / 2)){
                Matrix newMatrix = new Matrix(startMatrix);
                if (Configuration.instance.simulateSteps) {
                    //BacktrackingAlgorithm.threadDataAggreagtor.updateMatrixColumn(Thread.currentThread().getName(), 1, combination);
                }
                if (!this.checkOrthogonalityWithExistingColumns(newMatrix.getColumns(), combination, 1)) {
                    continue;
                }
                if (Configuration.instance.simulateSteps) {
                    BacktrackingAlgorithm.threadDataAggreagtor.updateMatrix(Thread.currentThread().getName(), newMatrix);
                }
                newMatrix.setColumn(combination, 1);
                if (this.solve(newMatrix)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean solve(Matrix sourceMatrix) throws InterruptedException {
        this.precheckConditions();
        int nextColumnIndex = sourceMatrix.getNextUnsetColumnIndex();
        if (nextColumnIndex == - 1){
            // Validate
            Matrix transpose = sourceMatrix.transpose();
            int[][] result = sourceMatrix.times(transpose);
            if (Helpers.isIdentity(result)){
                Configuration.instance.debugCounter.incrementAndGet();
                if (Configuration.instance.printDebugMessages) {
                    System.out.println("Found for dimension: " + Configuration.instance.dimension);
                    System.out.println(sourceMatrix.getDebugStringRepresentation());
                }
                threadDataAggreagtor.setResult(Thread.currentThread().getName(), sourceMatrix);
                return true;
            }
        } else {
            for (BigInteger i = BigInteger.ZERO; i.compareTo( BigInteger.valueOf(2).pow(sourceMatrix.getDimension() - 1)) < 0; i = i.add(BigInteger.ONE)) {
                BitSet combination = Helpers.convertTo(i);
                combination.set(sourceMatrix.getDimension() - 1);
                if ((combination.cardinality()) == (sourceMatrix.getDimension() / 2)){
                    Matrix newMatrix = new Matrix(sourceMatrix);
                    this.simulateStep(Thread.currentThread().getName(), newMatrix, combination, nextColumnIndex);

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

    private void simulateStep(String name, Matrix newMatrix, BitSet combination, int nextColumnIndex) throws InterruptedException {
        if (Configuration.instance.simulateSteps) {
            Thread.sleep(Configuration.instance.simulationStepDelayInMS);
            Matrix reportMatrix = new Matrix(newMatrix);
            reportMatrix.setColumn(combination, nextColumnIndex);
            //BacktrackingAlgorithm.threadDataAggreagtor.updateMatrixColumn(Thread.currentThread().getName(), sourceMatrix.getNextUnsetColumnIndex(), combination);
            BacktrackingAlgorithm.threadDataAggreagtor.updateMatrix(name, reportMatrix);
        }
    }

    private void precheckConditions() {
        if (threadDataAggreagtor.abortAllThreads.get()) {
            Thread.currentThread().interrupt();
        }
        if (Thread.currentThread().isInterrupted()){
            System.out.println(Thread.currentThread().toString() + " has been interrupted!");
            throw new CancellationException("Thread has been requested to stop");
        }
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
