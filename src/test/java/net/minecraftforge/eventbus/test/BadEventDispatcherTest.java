package net.minecraftforge.eventbus.test;

import cpw.mods.modlauncher.Launcher;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BadEventDispatcherTest {
    private boolean calledback;

    @BeforeAll
    public static void setup() {
        Configurator.setRootLevel(Level.DEBUG);
    }

    @Test
    void testBadEvent() {
        System.setProperty("test.harness", "");
        System.setProperty("test.harness.callable", "net.minecraftforge.eventbus.test.BadEventDispatcherTest$TestCallback");
        calledback = false;
        TestCallback.callable = () -> {
            calledback = true;
            final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            final Class<?> clazz = Class.forName("net.minecraftforge.eventbus.test.BadEventArmsLength", true, contextClassLoader);
            final Callable<Void> instance = (Callable<Void>) clazz.getConstructor().newInstance();
            instance.call();
            return null;
        };
        Launcher.main("--version", "1.0", "--launchTarget", "testharness");
        assertTrue(calledback, "We got called back");
    }

    public static class TestCallback {
        private static Callable<Void> callable;

        public static Callable<Void> supplier() {
            return callable;
        }
    }
}
