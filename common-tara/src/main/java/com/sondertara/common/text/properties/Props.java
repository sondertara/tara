package com.sondertara.common.text.properties;

import com.sondertara.common.collection.CollectionUtils;
import com.sondertara.common.collection.Maps;
import com.sondertara.common.function.Predicate2;
import com.sondertara.common.io.IOUtils;
import com.sondertara.common.io.resource.Resource;
import com.sondertara.common.io.resource.Resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class Props {
    private Props() {
    }

    public static Properties loadFromFile(File file) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return load(inputStream);
        } finally {
            IOUtils.close(inputStream);
        }
    }

    public static Properties loadFromFile(String location) throws IOException {
        return load(Resources.loadFileResource(location));
    }

    public static Properties loadFromClasspath(String classpath) throws IOException {
        return load(Resources.loadClassPathResource(classpath));
    }

    public static Properties loadFromClasspath(String classpath, ClassLoader classLoader) throws IOException {
        return load(Resources.loadClassPathResource(classpath, classLoader));
    }

    public static Properties loadFromURL(String url) throws IOException {
        return load(Resources.loadUrlResource(url));
    }

    public static Properties loadFromURL(URL url) throws IOException {
        return load(Resources.loadUrlResource(url));
    }

    public static Properties loadFromString(String string) throws IOException {
        return load(Resources.asByteArrayResource(string.getBytes(StandardCharsets.UTF_8)));
    }

    public static Properties load(Resource resource) throws IOException {
        Objects.requireNonNull(resource);
        InputStream inputStream = null;
        try {
            inputStream = resource.getInputStream();
            return load(inputStream);
        } finally {
            IOUtils.close(inputStream);
        }
    }

    public static Properties load(Reader reader) throws IOException {
        Properties props = new Properties();
        props.load(reader);
        return props;
    }

    public static Properties load(InputStream inputStream) throws IOException {
        Properties props = new Properties();
        props.load(inputStream);
        return props;
    }

    public static Properties loadFromXML(Resource resource) throws IOException {
        Objects.requireNonNull(resource);
        InputStream inputStream = null;
        try {
            inputStream = resource.getInputStream();
            return loadFromXML(inputStream);
        } finally {
            IOUtils.close(inputStream);
        }
    }

    public static Properties loadFromXML(InputStream inputStream) throws IOException {
        Properties props = new Properties();
        props.loadFromXML(inputStream);
        return props;
    }

    public static Map<String, String> filter(Properties properties, Predicate2<String, String> predicate) {
        Map<String, String> map = Maps.newStringMap(properties);
        return CollectionUtils.filter(map, predicate);
    }
}
