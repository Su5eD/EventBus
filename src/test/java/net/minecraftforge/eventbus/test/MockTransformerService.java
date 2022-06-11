package net.minecraftforge.eventbus.test;

import cpw.mods.jarhandling.SecureJar;
import cpw.mods.modlauncher.api.*;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MockTransformerService implements ITransformationService {

    @NotNull
    @Override
    public String name() {
        return "test";
    }

    @Override
    public void initialize(IEnvironment environment) {

    }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) throws IncompatibleEnvironmentException {

    }

    @Override
    public List<Resource> completeScan(IModuleLayerManager layerManager) {
        final SecureJar testJars = SecureJar.from(Path.of(System.getProperty("testJars.location")));
        final SecureJar self = LamdbaExceptionUtils.uncheck(() -> SecureJar.from(Path.of(getClass().getProtectionDomain().getCodeSource().getLocation().toURI())));
        return List.of(new Resource(IModuleLayerManager.Layer.GAME, List.of(testJars, self)));
    }

    @NotNull
    @Override
    public List<ITransformer> transformers() {
        return Collections.emptyList();
    }
}
