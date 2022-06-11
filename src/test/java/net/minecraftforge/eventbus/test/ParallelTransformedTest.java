package net.minecraftforge.eventbus.test;

import cpw.mods.modlauncher.Launcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

class ParallelTransformedTest {
    static final int LISTENER_COUNT = 1000;
    static final int RUN_ITERATIONS = 1000;

    static final AtomicLong COUNTER = new AtomicLong();

    @BeforeEach
    public void setup() {
        COUNTER.set(0);
    }

    @Disabled
    @RepeatedTest(100)
    void testOneBusParallelTransformed() {
        System.setProperty("test.harness", "");
        System.setProperty("test.harness.callable", "net.minecraftforge.eventbus.test.ParallelTransformedTest$TestCallback");
        Launcher.main("--version", "1.0", "--launchTarget", "testharness");
    }
    
    public static class TestCallback {
        public static Callable<Void> supplier() {
            final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                final Class<?> clazz = Class.forName("net.minecraftforge.eventbus.test.ArmsLengthHandler", true, contextClassLoader);
                return (Callable<Void>) clazz.getDeclaredConstructor().newInstance();
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
