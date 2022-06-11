package net.minecraftforge.eventbus.test;

import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class EventLambdaTest {
    boolean hit;
    
    @Test
    void eventLambda() {
        final IEventBus bus = BusBuilder.builder().build();
        bus.addListener((Event e)-> hit = true);
        bus.post(new Event());
        assertTrue(hit, "Hit event");
    }

    public void consumeSubEvent(SubEvent e) {
        hit = true;
    }
    
    @Test
    void eventSubLambda() {
        final IEventBus bus = BusBuilder.builder().build();
        bus.addListener(this::consumeSubEvent);
        bus.post(new SubEvent());
        assertTrue(hit, "Hit subevent");
        hit = false;
        bus.post(new Event());
        assertFalse(hit, "Didn't hit parent event");
    }

    @Test
    void eventGenericThing() {
        // pathological test because you can't derive the lambda types in all cases...
        final IEventBus bus = BusBuilder.builder().build();
        registerSomeGodDamnWrapper(bus, CancellableEvent.class, this::subEventFunction);
        final CancellableEvent event = new CancellableEvent();
        bus.post(event);
        assertTrue(event.isCanceled(), "Event got cancelled");
        final SubEvent subevent = new SubEvent();
        bus.post(subevent);
    }

    private boolean subEventFunction(final CancellableEvent event) {
        return event instanceof CancellableEvent;
    }

    public <T extends Event> void registerSomeGodDamnWrapper(IEventBus bus, Class<T> tClass, Function<T, Boolean> func) {
        bus.addListener(EventPriority.NORMAL, false, tClass, (T event) -> {
            if (func.apply(event)) {
                event.setCanceled(true);
            }
        });
    }

    public static class SubEvent extends Event {

    }

    public static class CancellableEvent extends Event {
        @Override
        public boolean isCancelable() {
            return true;
        }
    }
}
