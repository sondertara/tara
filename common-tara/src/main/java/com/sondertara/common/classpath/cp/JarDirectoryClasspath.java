package com.sondertara.common.classpath.cp;

import com.sondertara.common.classpath.Classpaths;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.io.file.FileFilters;
import com.sondertara.common.io.file.filter.FilenameSuffixFilter;
import com.sondertara.common.io.file.filter.IsFileFilter;
import com.sondertara.common.io.file.filter.ReadableFileFilter;
import com.sondertara.common.io.resource.DirectoryBasedFileResourceLoader;
import com.sondertara.common.io.resource.FileResource;
import com.sondertara.common.io.resource.Location;
import com.sondertara.common.io.resource.Resource;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * 扫描指定目录下所有的jar, zip
 * 不会递归扫描子目录
 *
 * @see DirectoryClasspath
 */
public class JarDirectoryClasspath extends AbstractClasspath {
    private List<JarFileClasspath> jars = new ArrayList<>();
    private Location root;

    public JarDirectoryClasspath(String dirName) {
        List<File> files = new DirectoryBasedFileResourceLoader(dirName)
                .listFiles(FileFilters.allFileFilter(
                        new IsFileFilter(),
                        new ReadableFileFilter(),
                        new FilenameSuffixFilter(new String[]{"jar", "zip"}, true)
                ));

        CollectionUtils.forEach(files, new BiConsumer<Integer, File>() {
            @Override
            public void accept(Integer index, File jarfile) {
                jars.add(new JarFileClasspath(jarfile));
            }
        });

        root = new Location(FileResource.PREFIX, dirName);
    }

    @Override
    public Resource findResource(final String relativePath) {
        final String path = Classpaths.getCanonicalFilePath(relativePath);
        return CollectionUtils.firstMap(jars, new BiFunction<Integer, JarFileClasspath, Resource>() {
            @Override
            public Resource apply(Integer index, JarFileClasspath jarClasspath) {
                return jarClasspath.findResource(path);
            }
        });
    }

    @Override
    public Location getRoot() {
        return root;
    }

    @Override
    public Set<Location> allResources() {
        final Set<Location> locations = new LinkedHashSet<>();
        CollectionUtils.forEach(jars, new BiConsumer<Integer, JarFileClasspath>() {
            @Override
            public void accept(Integer key, JarFileClasspath jarClasspath) {
                locations.addAll(jarClasspath.allResources());
            }
        });
        return locations;
    }
}
