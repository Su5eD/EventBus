module net.minecraftforge.eventbus.benchmarks {
    requires cpw.mods.modlauncher;
    requires cpw.mods.securejarhandler;
    requires jmh.core;
    requires powermock.core;
    requires powermock.reflect;

    requires net.minecraftforge.eventbus;
    requires net.minecraftforge.eventbus.testjars;

    exports net.minecraftforge.eventbus.benchmarks;
    
    provides cpw.mods.modlauncher.api.ITransformationService with net.minecraftforge.eventbus.benchmarks.MockTransformerService;
}