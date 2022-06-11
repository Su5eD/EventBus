package net.minecraftforge.eventbus.benchmarks;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.TransformingClassLoader;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.powermock.reflect.Whitebox;

import java.nio.file.Paths;
import java.util.function.Consumer;

@State(Scope.Benchmark)
public class EventBusBenchmark {
    private Runnable postStatic;
    private Runnable postDynamic;
    private Runnable postLambda;
    private Runnable postCombined;
    
    @Setup
    public void setup() throws Exception {
        //Forks have an incorrect working dir set, so use the absolute path to correct
        System.setProperty("test.harness", "");
        System.setProperty("test.harness.callable", "net.minecraftforge.eventbus.benchmarks.BenchmarkBootstrap");
        Launcher.main("--version", "1.0", "--launchTarget", "testharness");

        final ClassLoader tcl = Whitebox.getInternalState(Launcher.INSTANCE, "classLoader");
        final Class<?> cls = Class.forName("net.minecraftforge.eventbus.benchmarks.compiled.BenchmarkArmsLength", false, tcl);
        postStatic = (Runnable) cls.getDeclaredField("postStatic").get(null);
        postDynamic = (Runnable) cls.getDeclaredField("postDynamic").get(null);
        postLambda = (Runnable) cls.getDeclaredField("postLambda").get(null);
        postCombined = (Runnable) cls.getDeclaredField("postCombined").get(null);
    }

    @Benchmark
    public int testDynamic() {
        postDynamic.run();
        return 0;
    }

    @Benchmark
    public int testLambda() {
        postLambda.run();
        return 0;
    }

    @Benchmark
    public int testStatic() {
        postStatic.run();
        return 0;
    }
}
