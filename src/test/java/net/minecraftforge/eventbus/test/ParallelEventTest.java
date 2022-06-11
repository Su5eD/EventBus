package net.minecraftforge.eventbus.test;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraftforge.eventbus.ListenerList;
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.testjar.DummyEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.powermock.reflect.Whitebox;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

class ParallelEventTest {
    private static final int BUS_COUNT = 16;
    private static final int LISTENER_COUNT = 1000;
    private static final int RUN_ITERATIONS = 1000;

    private static final AtomicLong COUNTER = new AtomicLong();

    @BeforeEach
    public void setup() {
        COUNTER.set(0);
    }

    @Disabled
    @RepeatedTest(10)
    void testMultipleThreadsMultipleBus() {
        final Set<IEventBus> busSet = new HashSet<>();
        for (int i = 0; i < BUS_COUNT; i++) {
            busSet.add(BusBuilder.builder().setTrackPhases(false).build()); //make buses for concurrent testing
        }
        busSet.parallelStream().forEach(bus -> { //execute parallel listener adding
            for (int i = 0; i < LISTENER_COUNT; i++)
                bus.addListener(ParallelEventTest::handle);
        });
        busSet.parallelStream().forEach(LamdbaExceptionUtils.rethrowConsumer(bus -> { //post events parallel
            for (int i = 0; i < RUN_ITERATIONS; i++)
                bus.post(new DummyEvent.GoodEvent());
        }));
        final long expected = BUS_COUNT * LISTENER_COUNT * RUN_ITERATIONS;
        Assertions.assertEquals(COUNTER.get(), expected);
    }

    @Disabled
    @RepeatedTest(100)
    void testMultipleThreadsOneBus() {
        final IEventBus bus = BusBuilder.builder().setTrackPhases(false).build();

        Set<Runnable> toAdd = new HashSet<>();

        for (int i = 0; i < LISTENER_COUNT; i++) { //prepare parallel listener adding
            toAdd.add(() -> bus.addListener(ParallelEventTest::handle));
        }
        toAdd.parallelStream().forEach(Runnable::run); //execute parallel listener adding

        toAdd = new HashSet<>();
        for (int i = 0; i < RUN_ITERATIONS; i++) //prepare parallel event posting
            toAdd.add(() -> bus.post(new DummyEvent.GoodEvent()));
        toAdd.parallelStream().forEach(Runnable::run); //post events parallel

        try {
            final long expected = LISTENER_COUNT * RUN_ITERATIONS;
            final ListenerList listenerList = Whitebox.invokeMethod(new DummyEvent.GoodEvent(), "getListenerList");
            final int busid = Whitebox.getInternalState(bus, "busID");
            Assertions.assertAll(
                () -> Assertions.assertEquals(expected, COUNTER.get()),
                () -> Assertions.assertEquals(LISTENER_COUNT, listenerList.getListeners(busid).length - 1)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handle(DummyEvent.GoodEvent event) {
        COUNTER.incrementAndGet();
    }
}
