package com.sondertara.common.classpath.cp;

import com.sondertara.common.classpath.Classpaths;
import com.sondertara.common.collection.Sets;
import com.sondertara.common.io.resource.InputStreamResource;
import com.sondertara.common.io.resource.Location;
import com.sondertara.common.io.resource.Resource;
import com.sondertara.common.io.resource.Resources;

import java.io.InputStream;
import java.util.Set;

public class InputStreamClasspath extends AbstractClasspath {

    private InputStreamResource resource;

    public InputStreamClasspath(String desc, InputStream inputStream) {
        resource = Resources.asInputStreamResource(inputStream, desc);
    }

    @Override
    public Resource findResource(String relativePath) {
        relativePath = Classpaths.getCanonicalFilePath(relativePath);
        if (relativePath.equals(resource.getDescription())) {
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
