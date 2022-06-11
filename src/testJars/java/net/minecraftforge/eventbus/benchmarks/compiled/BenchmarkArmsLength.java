package net.minecraftforge.eventbus.benchmarks.compiled;

import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.concurrent.Callable;

public class BenchmarkArmsLength implements Callable<Void> {
    private static IEventBus staticSubscriberBus;
    private static IEventBus dynamicSubscriberBus;
    private static IEventBus lambdaSubscriberBus;
    private static IEventBus combinedSubscriberBus;

    public static final Runnable postStatic = () -> postAll(staticSubscriberBus);
    public static final Runnable postDynamic = () -> postAll(dynamicSubscriberBus);
    public static final Runnable postLambda = () -> postAll(lambdaSubscriberBus);
    public static final Runnable postCombined = () -> postAll(combinedSubscriberBus);

    @Override
    public Void call() {
        if (!new CancelableEvent().isCancelable())
            throw new RuntimeException("Transformer did not apply!");

        staticSubscriberBus = BusBuilder.builder().build();
        dynamicSubscriberBus = BusBuilder.builder().build();
        lambdaSubscriberBus = BusBuilder.builder().build();
        combinedSubscriberBus = BusBuilder.builder().build();

        staticSubscriberBus.register(SubscriberStatic.class);
        combinedSubscriberBus.register(SubscriberStatic.class);
        dynamicSubscriberBus.register(new SubscriberDynamic());
        combinedSubscriberBus.register(new SubscriberDynamic());
        SubscriberLambda.register(lambdaSubscriberBus);
        SubscriberLambda.register(combinedSubscriberBus);
        return null;
    }

    private static void postAll(IEventBus bus) {
        bus.post(new CancelableEvent());
        bus.post(new ResultEvent());
        bus.post(new EventWithData("Foo", 5, true)); //Some example data
    }
}
