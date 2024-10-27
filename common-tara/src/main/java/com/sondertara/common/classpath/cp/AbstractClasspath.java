package com.sondertara.common.classpath.cp;

import com.sondertara.common.base.Emptys;
import com.sondertara.common.classpath.ClassFile;
import com.sondertara.common.classpath.Classpath;
import com.sondertara.common.classpath.Classpaths;
import com.sondertara.common.classpath.ResourceClassFile;
import com.sondertara.common.classpath.ResourceFilter;
import com.sondertara.common.collection.StreamUtils;
import com.sondertara.common.io.resource.Location;
import com.sondertara.common.io.resource.Locations;
import com.sondertara.common.io.resource.Resource;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("rawtypes")
public abstract class AbstractClasspath implements Classpath {
    @Override
    public ClassFile findClassFile(String classname) {
        Resource resource = findResource(Classpaths.classNameToPath(classname));
        if (resource == null) {
            return null;
        }
        return new ResourceClassFile(resource);
    }

    @Override
    public List<ClassFile> scanClassFiles(String packageName, ResourceFilter filter) {
        List<Resource> resources = scanResources(packageName, filter);
        return resources.stream().map((Function<Resource, ClassFile>) resource -> {
            if (resource == null) {
                return null;
            }
            return new ResourceClassFile(resource);
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public List<Resource> scanResources(String namespace, ResourceFilter filter) {
        namespace = Classpaths.getCanonicalFilePath(namespace);
        Set<Location> locations = scanResourceLocations(namespace, filter);
        final Location root = getRoot();
        return StreamUtils.of(locations).map(new Function<Location, Resource>() {
            @Override
            public Resource apply(Location location) {
                String relativePath = Locations.getRelativePath(root, location);
                if (relativePath == null) {
                    return null;
                } else {
                    return findResource(relativePath);
                }
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 如果 namespace 是 null,则代表直接在 root 下递归检索
     *
     * @param namespace root下的 namespace
     * @param filter    filter
     * @return 搜索到的资源
     */
    @Override
    public Set<Location> scanResourceLocations(final String namespace, ResourceFilter filter) {

        final Location root = getRoot();
        final Location namespaceLocation = Locations.newLocation(root, namespace);
        Stream<Location> stream=allResources().stream();
        if (Emptys.isNotEmpty(namespace)) {
            stream=  stream.filter(
                    new Predicate<Location>() {
                        @Override
                        public boolean test(Location location) {
                            return Locations.getRelativePath(root, namespaceLocation) != null;
                        }
                    }
            );
        }
        if (filter != null) {
            stream = stream.filter(filter);
        }
        return stream.collect(Collectors.toSet());
    }


}
