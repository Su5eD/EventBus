package net.minecraftforge.eventbus.test;

import cpw.mods.modlauncher.Launcher;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GoodEventDispatcherTest {
    private boolean calledback;
    private Class<?> transformedClass;
    
    @BeforeAll
    public static void setup() {
        Configurator.setRootLevel(Level.DEBUG);
    }

    @Test
    void testGoodEvents() {
        System.setProperty("test.harness", "");
        System.setProperty("test.harness.callable", "net.minecraftforge.eventbus.test.GoodEventDispatcherTest$TestCallback");
        calledback = false;
        TestCallback.callable = () -> {
            calledback = true;
            final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            final Class<?> clazz = Class.forName("net.minecraftforge.eventbus.test.GoodEventArmsLength", true, contextClassLoader);
            final Callable<Class<?>> instance = (Callable<Class<?>>) clazz.getConstructor().newInstance();
            transformedClass = instance.call();
            return null;
        };
        Launcher.main("--version", "1.0", "--launchTarget", "testharness");
        assertTrue(calledback, "We got called back");
        assertAll(
            () -> assertTrue(WhiteboxImpl.getField(transformedClass, "HIT1").getBoolean(null), "HIT1 was hit"),
            () -> assertTrue(WhiteboxImpl.getField(transformedClass, "HIT2").getBoolean(null), "HIT2 was hit")
        );
    }

    public static class TestCallback {
        private static Callable<Void> callable;

        public static Callable<Void> supplier() {
            return callable;
        }
    }
}
