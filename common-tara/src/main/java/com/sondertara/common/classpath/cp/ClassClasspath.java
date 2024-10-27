package com.sondertara.common.classpath.cp;

import com.sondertara.common.classpath.Classpaths;
import com.sondertara.common.collection.Sets;
import com.sondertara.common.io.resource.ClassPathResource;
import com.sondertara.common.io.resource.Location;
import com.sondertara.common.io.resource.Resource;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Set;

public class ClassClasspath extends AbstractClasspath {

    /**
     * 基于这个 Class 去加载
     */
    private Class clazz;
    private Location root = new Location(ClassPathResource.PREFIX, "/");

    public ClassClasspath(@NonNull Class clazz) {
        Objects.requireNonNull(clazz);
        this.clazz = clazz;
    }

    @Override
    public Resource findResource(String relativePath) {
        relativePath = Classpaths.getCanonicalFilePath(relativePath);
        return new ClassPathResource(relativePath, this.clazz);
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
