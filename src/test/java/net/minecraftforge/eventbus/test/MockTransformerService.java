package net.minecraftforge.eventbus.test;

import cpw.mods.jarhandling.SecureJar;
import cpw.mods.modlauncher.api.*;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

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
    public List<Resource> beginScanning(IEnvironment environment) {
        SecureJar testjar = SecureJar.from(Path.of(System.getProperty("testJars.location")));
        return List.of(new Resource(IModuleLayerManager.Layer.PLUGIN, List.of(testjar)));
    }

    @Override
    public List<Resource> completeScan(IModuleLayerManager layerManager) {
        SecureJar testjar = SecureJar.from(Path.of(System.getProperty("testJars.location")));
        return List.of(new Resource(IModuleLayerManager.Layer.GAME, List.of(testjar)));
    }

    @NotNull
    @Override
    public List<ITransformer> transformers() {
        return Collections.emptyList();
    }

}
