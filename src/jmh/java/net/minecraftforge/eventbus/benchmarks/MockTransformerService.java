package net.minecraftforge.eventbus.benchmarks;

import cpw.mods.jarhandling.SecureJar;
import cpw.mods.modlauncher.api.*;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MockTransformerService implements ITransformationService {

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
        final SecureJar testjar = SecureJar.from(Path.of(System.getProperty("testJars.location")));
        return List.of(new Resource(IModuleLayerManager.Layer.GAME, List.of(testjar)));
    }

    @Override
    public List<ITransformer> transformers() {
        return Collections.emptyList();
    }

}
