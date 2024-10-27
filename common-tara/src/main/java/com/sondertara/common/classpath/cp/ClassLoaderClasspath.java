package com.sondertara.common.classpath.cp;

import com.sondertara.common.classpath.Classpaths;
import com.sondertara.common.collection.Sets;
import com.sondertara.common.io.resource.ClassPathResource;
import com.sondertara.common.io.resource.Location;
import com.sondertara.common.io.resource.Resource;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Set;

public class ClassLoaderClasspath extends AbstractClasspath {
    private ClassLoader classLoader;
    private Location root = new Location(ClassPathResource.PREFIX,"");

    public ClassLoaderClasspath(@NonNull ClassLoader loader) {
        Objects.requireNonNull(loader);
        this.classLoader = loader;
    }

    @Override
    public Resource findResource(String relativePath) {
        relativePath = Classpaths.getCanonicalFilePath(relativePath);
        return new ClassPathResource(relativePath, classLoader);
    }

    @Override
    public Location getRoot() {
        return root;
    }

    @Override
    public Set<Location> allResources() {
        return Sets.immutableSet();
    }
}
