package net.minecraftforge.eventbus.test;

import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class ThreadedListenerExceptionTest {
    private static boolean failed;

    private static final IEventBus TEST_EVENT_BUS = BusBuilder.builder().setExceptionHandler((bus, event, listeners, index, throwable) -> {
        failed = true;
        throwable.printStackTrace();
    }).build();
    
    private static ExecutorService executorService;

    @BeforeAll
    static void beforeClass() {
        executorService = Executors.newFixedThreadPool(100);
    }
    
    @BeforeEach
    public void beforeEach() throws Exception {
        failed = false;
        final List<Callable<Object>> callables = Collections.nCopies(50, Executors.callable(() -> TEST_EVENT_BUS.addListener(ThreadedListenerExceptionTest::testEvent)));
        executorService.invokeAll(callables).forEach(f -> {
            try {
                // wait for everybody
                f.get();
            } catch (InterruptedException | ExecutionException ignored) {
            }
        });
    }

    @Disabled
    @RepeatedTest(100)
    void testWithTimeout() {
        assertTimeoutPreemptively(Duration.ofMillis(10000), this::testListenerList);
    }

    private void testListenerList() throws Exception {
        final List<Callable<Object>> callables = Collections.nCopies(100, Executors.callable(ThreadedListenerExceptionTest::generateEvents));
        executorService.invokeAll(callables).forEach(f -> {
            try {
                // wait for everybody
                f.get();
            } catch (InterruptedException | ExecutionException ignored) {
            }
        });
        assertFalse(failed);
    }

    private static void generateEvents() {
        for (int i = 0; i < 10; i ++) {
            TEST_EVENT_BUS.post(new TestEvent());
        }
    }
    
    private static void testEvent(TestEvent evt) {

    }

    public static class TestEvent extends Event {
        public TestEvent() {}
    }
}
