package hadamard;

import java.util.concurrent.atomic.AtomicInteger;

public enum Configuration {
    instance;
    final int maximumNumberOfThreads = Runtime.getRuntime().availableProcessors();

    public final long maxTimeOutInSeconds = 900;
    public int dimension = 2;

    public AtomicInteger debugCounter = new AtomicInteger();
}
