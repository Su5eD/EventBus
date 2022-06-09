open module net.minecraftforge.eventbus.test {
    requires cpw.mods.modlauncher;
    requires static net.minecraftforge.eventbus.testjars;

    requires org.junit.jupiter.api;
    requires powermock.core;
    requires powermock.reflect;
    requires org.objectweb.asm;
    requires org.objectweb.asm.tree;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires net.minecraftforge.eventbus;
    requires cpw.mods.securejarhandler;
    requires org.jetbrains.annotations;

    exports net.minecraftforge.eventbus.test;
    provides cpw.mods.modlauncher.api.ITransformationService with net.minecraftforge.eventbus.test.MockTransformerService;

}