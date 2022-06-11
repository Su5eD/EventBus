package net.minecraftforge.eventbus.test;

import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.testjar.DummyEvent;
import net.minecraftforge.eventbus.testjar.EventBusTestClass;
import org.junit.jupiter.api.Assertions;

import java.util.concurrent.Callable;

public class GoodEventArmsLength implements Callable<Class<?>> {
    @Override
    public Class<?> call() throws Exception {
        final IEventBus bus = BusBuilder.builder().build();
        bus.register(new EventBusTestClass());
        Assertions.assertDoesNotThrow(() -> bus.post(new DummyEvent.GoodEvent()));
        return EventBusTestClass.class;
    }
}
