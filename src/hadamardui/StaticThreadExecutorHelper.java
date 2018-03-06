package hadamardui;

import hadamard.ThreadDataAggregator;

public class StaticThreadExecutorHelper {
    public static void setModel(HadamardModel model) {
        StaticThreadExecutorHelper.model = model;
    }

    private static HadamardModel model;

    public static void setAggregator(ThreadDataAggregator aggregator) {
        StaticThreadExecutorHelper.aggregator = aggregator;
    }

    private static ThreadDataAggregator aggregator;

    public static void execute(){
        model.execute(aggregator);
    }


}
