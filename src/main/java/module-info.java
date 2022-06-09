module net.minecraftforge.eventbus {
    requires static org.jetbrains.annotations;
    requires org.apache.logging.log4j;
    requires org.objectweb.asm;
    requires org.objectweb.asm.tree;
    requires net.jodah.typetools;
    requires cpw.mods.modlauncher;

    exports net.minecraftforge.eventbus;
    exports net.minecraftforge.eventbus.api;
    exports net.minecraftforge.eventbus.service;

    provides cpw.mods.modlauncher.serviceapi.ILaunchPluginService with net.minecraftforge.eventbus.service.ModLauncherService;
}