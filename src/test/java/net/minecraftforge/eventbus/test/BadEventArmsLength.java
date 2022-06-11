package net.minecraftforge.eventbus.test;

import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.testjar.DummyEvent;
import net.minecraftforge.eventbus.testjar.EventBusTestClass;
import org.junit.jupiter.api.Assertions;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.concurrent.Callable;

public class BadEventArmsLength implements Callable<Void> {
    @Override
    public Void call() throws Exception {
        final IEventBus bus = BusBuilder.builder().build();
        bus.register(new EventBusTestClass());
        Assertions.assertThrows(
            RuntimeException.class,
            () -> WhiteboxImpl.invokeMethod(bus, "post", new DummyEvent.BadEvent()),
            "We got the exception"
        );
        return null;
    }
}
