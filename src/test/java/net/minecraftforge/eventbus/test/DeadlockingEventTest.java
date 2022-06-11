package net.minecraftforge.eventbus.test;

import cpw.mods.modlauncher.Launcher;
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventListenerHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.*;
import org.powermock.reflect.Whitebox;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DeadlockingEventTest {
    public static final int BOUND = 10000;
    private static final boolean INITIALIZE_AT_CLASSLOADING = false;
    private static final long WAIT_TIMEOUT = 1; // number of seconds to wait before retrying. Bump this up to debug what's going on.
    
    public static ExecutorService threadPool;

    @BeforeAll
    static void setup() {
        // force async logging
        System.setProperty("log4j2.contextSelector","org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        System.setProperty("test.harness", "");
        System.setProperty("test.harness.callable", "net.minecraftforge.eventbus.test.DeadlockingEventTest$Callback");
    }

    @Disabled
    @RepeatedTest(500)
    void testConstructEventDeadlock() {
        Launcher.main("--version", "1.0", "--launchTarget", "testharness");
    }

    @BeforeEach
    void newThreadPool() {
        threadPool = Executors.newFixedThreadPool(2);
    }
    
    @AfterEach
    void clearBusStuff() throws Exception {
        Whitebox.invokeMethod(EventListenerHelper.class, "clearAll");
        final HashSet<AbstractQueuedSynchronizer> workers = Whitebox.getInternalState(threadPool, "workers");
        workers.stream()
            .map(w -> Whitebox.<Thread>getInternalState(w, "thread"))
            .map(Thread::getStackTrace)
            .forEach(ts->LogManager.getLogger().info("\n"+stack(ts)));
        threadPool.shutdown();
    }

    private static String stack(StackTraceElement[] elts) {
        final StringBuilder sb = new StringBuilder();
        for (StackTraceElement elt : elts) {
            sb.append("\tat ").append(elt).append("\n");
        }
        return sb.toString();
    }
    
    public static class Callback {
        public static Callable<Void> supplier() {
            return () -> {
                final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                LogManager.getLogger().info("Class Loader {}", contextClassLoader);
                final CountDownLatch cdl = new CountDownLatch(1);
                final IEventBus bus = BusBuilder.builder().build();
                Callable<Void> task2 = () -> {
                    final int nanos = new Random().nextInt(BOUND);
                    LogManager.getLogger().info("Task 2: {}", nanos);
                    final long start = System.nanoTime();
                    cdl.await();
                    final Class<? extends Event> clz = (Class<? extends Event>) Class.forName("net.minecraftforge.eventbus.test.DeadlockingEventArmsLength$ChildEvent", INITIALIZE_AT_CLASSLOADING, contextClassLoader);
                    Thread.sleep(0, nanos);
                    LogManager.getLogger().info(System.nanoTime() - start);
                    assertEquals(clz.getConstructor().newInstance().getListenerList(), EventListenerHelper.getListenerList(clz));
                    LogManager.getLogger().info("Task 2");
                    return null;
                };
                Callable<Void> task1 = () -> {
                    final int nanos = new Random().nextInt(BOUND);
                    LogManager.getLogger().info("Task 1: {}", nanos);
                    final long start = System.nanoTime();
                    cdl.await();
                    final Class<?> clz = Class.forName("net.minecraftforge.eventbus.test.DeadlockingEventArmsLength$Listener1", INITIALIZE_AT_CLASSLOADING, contextClassLoader);
                    Thread.sleep(0, nanos);
                    LogManager.getLogger().info(System.nanoTime() - start);
                    bus.register(clz);
                    LogManager.getLogger().info("Task 1");
                    return null;
                };
                final List<Future<Void>> futures = Stream.of(task1, task2).map(threadPool::submit).toList();
                cdl.countDown();
                try {
                    assertTimeoutPreemptively(Duration.ofSeconds(WAIT_TIMEOUT), () -> futures.parallelStream().forEach(f -> {
                        try {
                            f.get();
                        } catch (InterruptedException | ExecutionException e) {
                            fail("error", e);
                        }
                    }));
                } finally {
                    futures.forEach(f -> f.cancel(true));
                    futures.forEach(f -> {
                        try {
                            f.get();
                        } catch (CancellationException | InterruptedException | ExecutionException e) {
                            // noop
                        }
                    });
                }
                return null;
            };
        };
    }
}
