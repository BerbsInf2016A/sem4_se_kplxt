package hadamard;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadDataAggregator {

    private static ConcurrentHashMap<String,Matrix> currentThreadMatrixState
            = new ConcurrentHashMap<>(Configuration.instance.dimension);
    public static AtomicBoolean resultFound = new AtomicBoolean();
    public static AtomicBoolean abortAllThreads = new AtomicBoolean();
    public static String resultThreadName = "";
    public static Matrix resultMatrix = null;

    public void updateMatrix(String threadName, Matrix matrix) {
        currentThreadMatrixState.put(threadName, matrix);
        System.out.println(matrix.getUIDebugStringRepresentation());
    }


    public void setResult(String threadName, Matrix matrix) {
        resultFound.set(true);
        if (Configuration.instance.abortAfterFirstResult){
            abortAllThreads.set(true);
        }
        resultThreadName = threadName;
        resultMatrix = matrix;
    }




}
