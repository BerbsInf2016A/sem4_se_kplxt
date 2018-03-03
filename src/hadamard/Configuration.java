package hadamard;

public enum Configuration {
    instance;
    final int maximumNumberOfThreads = Runtime.getRuntime().availableProcessors();

    public final long maxTimeOutInSeconds = 900;
}
