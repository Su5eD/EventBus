package net.minecraftforge.eventbus.testjar;

import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventBusTestClass {
    public static boolean HIT1 = false;
    public static boolean HIT2 = false;
    
    @SubscribeEvent
    public void eventMethod(DummyEvent evt) {
        HIT1 = true;
    }

    @SubscribeEvent
    void eventMethod2(DummyEvent.GoodEvent evt) {
        HIT2 = true;
    }

    @SubscribeEvent
    public void evtMethod3(DummyEvent.CancellableEvent evt) {

    }

    @SubscribeEvent
    public void evtMethod4(DummyEvent.ResultEvent evt) {

    }


    @SubscribeEvent
    public void badEventMethod(DummyEvent.BadEvent evt) {
        throw new RuntimeException("BARF");
    }
}
