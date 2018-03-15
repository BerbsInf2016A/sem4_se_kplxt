package hadamardui;

import hadamard.ThreadDataAggregator;

public class StaticThreadExecutorHelper {
    private static HadamardModel model;
    private static ThreadDataAggregator aggregator;

    public static void setModel(HadamardModel model) {
        StaticThreadExecutorHelper.model = model;
    }

    public static void setAggregator(ThreadDataAggregator aggregator) {
        StaticThreadExecutorHelper.aggregator = aggregator;
    }

    public static void execute() {
        model.execute(aggregator);
    }


}
