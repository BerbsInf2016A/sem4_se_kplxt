package hadamard;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadDataAggregator {

    public static AtomicBoolean resultFound = new AtomicBoolean();
    public static AtomicBoolean abortAllThreads = new AtomicBoolean();
    public static List<String> threadsWithResults = Collections.synchronizedList(new ArrayList());

    private List<IMatrixChangedListener> listeners;

    public void updateMatrix(String threadName, Matrix matrix) {
        if (Configuration.instance.printDebugMessages)
            System.out.println(matrix.getUIDebugStringRepresentation());
        for (IMatrixChangedListener listener : this.listeners ) {
            listener.matrixChanged(threadName, matrix);
        }
    }

    public void updateMatrixColumn(String threadName, int columnIndex, BitSet column){
        for (IMatrixChangedListener listener : this.listeners ) {
            listener.matrixColumnChanged(threadName, columnIndex, column);
        }
    }


    public void setResult(String threadName, Matrix matrix) {
        if (Configuration.instance.printDebugMessages) {
            System.out.println(threadName + " setting result");
        }
        resultFound.set(true);

        if (!threadsWithResults.contains(threadName))
            threadsWithResults.add(threadName);

        this.notifyResultFound(threadName, matrix);
        if (Configuration.instance.abortAfterFirstResult){
            abortAllThreads.set(true);
        }
    }
    public void setApplicationState(ApplicationState state){
        
    }

    private void notifyResultFound(String threadName, Matrix matrix) {
        for (IMatrixChangedListener listener : this.listeners ) {
            listener.resultFound(threadName, matrix);
        }
    }

    public boolean threadAlreadyFoundResult(String threadName){
        return threadsWithResults.contains(threadName);
    }

    public void reset() {
        ThreadDataAggregator.abortAllThreads.set(false);
        ThreadDataAggregator.resultFound.set(false);
        ThreadDataAggregator.threadsWithResults = Collections.synchronizedList(new ArrayList());
    }

    public ThreadDataAggregator(){
        this.listeners = new ArrayList<>();
    }

    public void registerListener(IMatrixChangedListener listener){
        if (!this.listeners.contains(listener)){
            this.listeners.add(listener);
        }
    }

    public void removeListener(IMatrixChangedListener listener){
        if (this.listeners.contains(listener)){
            this.listeners.remove(listener);
        }
    }
}
