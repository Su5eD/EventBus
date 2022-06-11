package net.minecraftforge.eventbus.benchmarks;

import cpw.mods.modlauncher.Launcher;
import org.powermock.reflect.Whitebox;

import java.util.concurrent.Callable;

public class BenchmarkBootstrap {
    @SuppressWarnings("unchecked")
    public static Callable<Void> supplier() throws Exception {
        final ClassLoader tcl = Whitebox.getInternalState(Launcher.INSTANCE, "classLoader");
        try {
            final Class<?> clazz = Class.forName("net.minecraftforge.eventbus.benchmarks.compiled.BenchmarkArmsLength", true, tcl);
            return (Callable<Void>) clazz.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }
}
