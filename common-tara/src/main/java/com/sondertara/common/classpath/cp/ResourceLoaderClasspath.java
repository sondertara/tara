package com.sondertara.common.classpath.cp;

import com.sondertara.common.classpath.Classpaths;
import com.sondertara.common.io.resource.Location;
import com.sondertara.common.io.resource.Resource;
import com.sondertara.common.io.resource.ResourceLoader;

import java.util.HashSet;
import java.util.Set;

public class ResourceLoaderClasspath extends AbstractClasspath {
    private ResourceLoader loader;

    public ResourceLoaderClasspath(ResourceLoader loader) {
        this.loader = loader;
    }

    @Override
    public Resource findResource(String relativePath) {
        relativePath = Classpaths.getCanonicalFilePath(relativePath);
        return loader.loadResource(relativePath);
    }

    @Override
    public Location getRoot() {
        return null;
    }

    @Override
    public Set<Location> allResources() {
        return new HashSet<>();
    }
}
