package com.sondertara.common.classpath.cp;

import com.sondertara.common.classpath.Classpaths;
import com.sondertara.common.collection.Sets;
import com.sondertara.common.io.resource.ByteArrayResource;
import com.sondertara.common.io.resource.Location;
import com.sondertara.common.io.resource.Resource;
import com.sondertara.common.io.resource.Resources;
import org.jspecify.annotations.NonNull;

import java.util.Set;

public class ByteArrayClasspath extends AbstractClasspath {
    private ByteArrayResource resource;

    public ByteArrayClasspath(String desc, byte[] bytes) {
        resource = Resources.asByteArrayResource(bytes, desc);
    }

    @Override
    public Resource findResource(@NonNull String relativePath) {
        relativePath = Classpaths.getCanonicalFilePath(relativePath);
        if (relativePath.equals(resource.getPath())) {
            return resource;
        }
        return null;
    }

    @Override
    public Location getRoot() {
        return resource.getLocation();
    }

    @Override
    public Set<Location> allResources() {
        return Sets.newHashSet(resource.getLocation());
    }
}
