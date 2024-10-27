package com.sondertara.common.classpath.cp;

import com.sondertara.common.base.Emptys;
import com.sondertara.common.classpath.Classpaths;
import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.io.IOUtils;
import com.sondertara.common.io.file.Filenames;
import com.sondertara.common.io.resource.Location;
import com.sondertara.common.io.resource.Locations;
import com.sondertara.common.io.resource.Resource;
import com.sondertara.common.io.resource.ResourceNotFoundException;
import com.sondertara.common.io.resource.Resources;
import com.sondertara.common.net.URLs;
import com.sondertara.common.text.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class WarFileClasspath extends AbstractClasspath {

    public static final String FILE_NAME_PREFIX = "WEB-INF/classes/";

    /**
     * 只是存放了  WEB-INF/classes 目录下的文件名
     * key: file suffix
     * values: file path relative the jar
     */
    private Map<String, Set<String>> fileEntries = CollectionUtils.emptyNonAbsentHashMap(new Function<String, Set<String>>() {
        @Override
        public Set<String> apply(String key) {
            return Collections.emptySet();
        }
    });
    private String jarfileURL;
    private Location root;

    private String getSuffix(String path) {
        String suffix = Filenames.getSuffix(path);
        return Emptys.isNotEmpty(suffix) ? suffix : "__langx_other__";
    }

    public WarFileClasspath(String jarPath) {
        this(new File(jarPath));
    }

    public WarFileClasspath(File file) {
        if (file.exists() && file.isFile() && file.canRead()) {
            JarFile jarfile = null;
            try {
                jarfile = new JarFile(file);
                for (JarEntry entry : Collections.list(jarfile.entries())) {
                    if (!entry.isDirectory()) {
                        if (entry.getName().startsWith(FILE_NAME_PREFIX)) {
                            String suffix = getSuffix(entry.getName());
                            fileEntries.get(suffix).add(entry.getName().substring(FILE_NAME_PREFIX.length()));
                        }
                    }
                }

                this.jarfileURL = file.getCanonicalFile().toURI().toURL().toString();
            } catch (IOException e) {
                throw new ResourceNotFoundException(file.getName());
            } finally {
                IOUtils.close(jarfile);
            }
        }

        root = new Location(URLs.URL_PREFIX_JAR, jarfileURL + URLs.JAR_URL_SEPARATOR + "WEB-INF/classes");
    }

    @Override
    public Resource findResource(String relativePath) {
        relativePath = Classpaths.getCanonicalFilePath(relativePath);
        String suffix = getSuffix(relativePath);
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        if (this.fileEntries.get(suffix).contains(relativePath)) {
            String url = getUrl(relativePath);
            return Resources.loadUrlResource(url);
        }
        return null;
    }

    @Override
    public Location getRoot() {
        return root;
    }

    @Override
    public Set<Location> allResources() {
        Set<Location> result = new HashSet<>();

        for (Set<String> set : fileEntries.values()) {
            set.stream().forEach(s -> {
                String relativePath = Classpaths.getCanonicalFilePath(s);
                Location location = Locations.newLocation(root, relativePath);
                result.add(location);
            });
        }

        return result;
    }

    private String getUrl(String relativePath) {
        relativePath = Classpaths.getCanonicalFilePath(relativePath);
        return StringUtils.format("{}/{}", this.root.getLocation(), relativePath);
    }
}
