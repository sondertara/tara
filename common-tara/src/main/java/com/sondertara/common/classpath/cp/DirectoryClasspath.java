package com.sondertara.common.classpath.cp;

import com.sondertara.common.classpath.Classpaths;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.io.file.Files;
import com.sondertara.common.io.resource.DirectoryBasedFileResourceLoader;
import com.sondertara.common.io.resource.FileResource;
import com.sondertara.common.io.resource.Location;
import com.sondertara.common.io.resource.Resource;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

/**
 * 普通的目录，不会扫描它下面的 jar,zip文件
 *
 * @see JarDirectoryClasspath
 */
public class DirectoryClasspath extends AbstractClasspath {
    private DirectoryBasedFileResourceLoader loader;
    private Location root;

    public DirectoryClasspath(String rootDirectory) {
        this.root = new Location(FileResource.PREFIX, rootDirectory, "/");
        this.loader = new DirectoryBasedFileResourceLoader(rootDirectory);
    }

    @Override
    public Resource findResource(String relativePath) {
        relativePath = Classpaths.getCanonicalFilePath(relativePath);
        return loader.loadResource(relativePath);
    }

    @Override
    public Location getRoot() {
        return root;
    }

    @Override
    public Set<Location> allResources() {
        File rootFile = new File(root.getPath());
        Set<Location> locations = new TreeSet<>();
        if (rootFile.exists() && rootFile.isDirectory()) {
            scan(locations, rootFile);
        }
        return locations;
    }

    private void scan(@NonNull final Set<Location> results, @NonNull final File current) {
        if (current.isFile()) {
            results.add(new Location(FileResource.PREFIX, Files.getCanonicalPath(current)));
        } else {
            CollectionUtils.forEach(current.listFiles(), new Consumer<File>() {
                @Override
                public void accept(File file) {
                    scan(results, file);
                }
            });
        }
    }
}
