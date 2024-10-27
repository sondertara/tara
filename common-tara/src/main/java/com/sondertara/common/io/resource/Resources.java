package com.sondertara.common.io.resource;

import com.sondertara.common.text.StringUtils;
import com.sondertara.common.exception.ExceptionUtils;
import com.sondertara.common.function.Predicate2;
import com.sondertara.common.io.Channels;
import com.sondertara.common.io.IOUtils;
import com.sondertara.common.io.file.Files;
import com.sondertara.common.net.URLs;
import com.sondertara.common.struct.Holder;
import com.sondertara.common.text.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Resources {
    private Resources() {

    }

    public static <V extends Resource> V loadResource(@NonNull Location location) {
        return Locations.newResource(location.toString());
    }

    public static <V extends Resource> V loadResource(@NonNull String location) {
        return loadResource(location, null);
    }

    public static <V extends Resource> V loadResource(@NonNull String location, @Nullable ClassLoader classLoader) {
        return new DefaultResourceLoader(classLoader).loadResource(location);
    }

    public static FileResource loadFileResource(@NonNull File file) {
        return loadFileResource(Files.getCanonicalPath(file), null);
    }

    public static FileResource loadFileResource(@NonNull String location) {
        return loadFileResource(location, null);
    }

    public static FileResource loadFileResource(@NonNull String location, @Nullable ClassLoader classLoader) {
        Objects.requireNonNull(location);
        if (!StringUtils.startsWith(location, FileResource.PREFIX)) {
            location = FileResource.PREFIX + location;
        }
        return new DefaultResourceLoader(classLoader).loadResource(location);
    }


    public static ClassPathResource loadClassPathResource(@NonNull String location) {
        return loadClassPathResource(location, (ClassLoader) null);
    }

    public static ClassPathResource loadClassPathResource(@NonNull String location, @Nullable ClassLoader classLoader) {
        Objects.requireNonNull(location);
        if (!StringUtils.startsWith(location, ClassPathResource.PREFIX)) {
            location = ClassPathResource.PREFIX + location;
        }
        return new DefaultResourceLoader(classLoader).loadResource(location);
    }

    public static ClassPathResource loadClassPathResource(@NonNull String location, @Nullable Class clazz) {
        Objects.requireNonNull(location);
        if (!StringUtils.startsWith(location, ClassPathResource.PREFIX)) {
            location = ClassPathResource.PREFIX + location;
        }
        return new ClassPathResource(location, clazz);
    }

    public static UrlResource loadUrlResource(@NonNull String location) {
        return loadUrlResource(location, null);
    }

    public static UrlResource loadUrlResource(@NonNull String location, @Nullable ClassLoader classLoader) {
        Objects.requireNonNull(URLs.newURL(location), StringUtils.format("location : {} not a URL", location));
        return new DefaultResourceLoader(classLoader).loadResource(location);
    }

    public static UrlResource loadUrlResource(@NonNull URL url) {
        return new UrlResource(url);
    }


    public static ByteArrayResource asByteArrayResource(@NonNull byte[] byteArray) {
        return new ByteArrayResource(byteArray);
    }

    public static ByteArrayResource asByteArrayResource(@NonNull byte[] byteArray, @Nullable String description) {
        return new ByteArrayResource(byteArray, description);
    }

    public static InputStreamResource asInputStreamResource(@NonNull InputStream inputStream) {
        return new InputStreamResource(inputStream);
    }

    public static InputStreamResource asInputStreamResource(@NonNull InputStream inputStream, @Nullable String description) {
        return new InputStreamResource(inputStream, description);
    }

    /**
     * @param resource the resource location
     * @return whether the resource exists or not
     *      */
    public static boolean exists(String resource) {
        Resource res = loadResource(resource);
        if (res == null) {
            return false;
        }
        return res.exists();
    }

    /**
     * @param resource the resource
     * @return the input stream
     *      */
    public static InputStream getInputStream(String resource) throws IOException {
        Resource res = loadResource(resource);
        if (res == null) {
            throw new IOException(StringUtils.format("Can't find the resource: {}", resource));
        }
        return res.getInputStream();
    }


    public static void readUsingDelimiter(Resource resource, @NonNull String delimiter, @NonNull final Consumer<byte[]> consumer) {
        InputStream inputStream = null;
        try {
            inputStream = resource.getInputStream();
            Channels.readUsingDelimiter(inputStream, delimiter, consumer);
        } catch (IOException ex) {
            throw ExceptionUtils.wrapAsRuntimeException(ex);
        } finally {
            IOUtils.close(inputStream);
        }
    }

    public static void readUsingDelimiter(Resource resource, @NonNull String delimiter, @NonNull final Charset charset, @NonNull final Consumer<String> consumer) {
        readUsingDelimiter(resource, delimiter, charset, new BiConsumer<Integer, String>() {
            @Override
            public void accept(Integer index, String value) {
                consumer.accept(value);
            }
        });
    }

    public static void readUsingDelimiter(Resource resource, @NonNull String delimiter, @NonNull final Charset charset, @NonNull final BiConsumer<Integer, String> consumer) {
        readUsingDelimiter(resource, delimiter, charset, null, consumer, null);
    }

    public static void readUsingDelimiter(Resource resource, @NonNull String delimiter, @NonNull final Charset charset, @Nullable Predicate2<Integer, String> consumePredicate, @NonNull final BiConsumer<Integer, String> consumer, @Nullable Predicate2<Integer, String> breakPredicate) {
        ReadableByteChannel channel = null;
        try {
            channel = resource.readableChannel();
            Channels.readUsingDelimiter(channel, delimiter, charset, consumePredicate, consumer, breakPredicate);
        } catch (IOException ex) {
            throw ExceptionUtils.wrapAsRuntimeException(ex);
        } finally {
            IOUtils.close(channel);
        }
    }


    public static void readUsingDelimiter(@NonNull String location, @NonNull String delimiter, @NonNull final Charset charset, @NonNull final Consumer<String> consumer) {
        Objects.requireNonNull(location);
        Resource resource = loadResource(location);
        if (resource.exists() && resource.isReadable()) {
            readUsingDelimiter(resource, delimiter, charset, consumer);
        }
    }

    public static void readUsingDelimiter(@NonNull String location, @NonNull String delimiter, @NonNull final Charset charset, @NonNull final BiConsumer<Integer, String> consumer) {
        Objects.requireNonNull(location);
        Resource resource = loadResource(location);
        if (resource.exists() && resource.isReadable()) {
            readUsingDelimiter(resource, delimiter, charset, consumer);
        }
    }

    public static void readUsingDelimiter(@NonNull String location, @NonNull String delimiter, @NonNull final Charset charset, @Nullable Predicate2<Integer, String> consumePredicate, @NonNull final BiConsumer<Integer, String> consumer, @Nullable Predicate2<Integer, String> breakPredicate) {
        Objects.requireNonNull(location);
        Resource resource = loadResource(location);
        if (resource.exists() && resource.isReadable()) {
            readUsingDelimiter(resource, delimiter, charset, consumePredicate, consumer, breakPredicate);
        }
    }


    public static void readUsingDelimiter(@NonNull URL url, @NonNull String delimiter, @NonNull final Charset charset, @NonNull final Consumer<String> consumer) {
        readUsingDelimiter(loadUrlResource(url), delimiter, charset, consumer);
    }

    public static void readUsingDelimiter(@NonNull byte[] byteArray, @NonNull String delimiter, @NonNull final Charset charset, @NonNull final Consumer<String> consumer) {
        Resource resource = asByteArrayResource(byteArray);
        if (resource.exists() && resource.isReadable()) {
            readUsingDelimiter(resource, delimiter, charset, consumer);
        }
    }

    public static List<String> readLines(@NonNull Resource resource, @NonNull Charset charset) {
        final List<String> lines = new ArrayList<>();
        readUsingDelimiter(resource, "\n", charset, new Consumer<String>() {
            @Override
            public void accept(String line) {
                lines.add(line);
            }
        });
        return lines;
    }

    public static void readLines(@NonNull Resource resource, @NonNull Charset charset, Consumer<String> consumer) {
        readUsingDelimiter(resource, "\n", charset, consumer);
    }

    public static void readLines(@NonNull Resource resource, @NonNull Charset charset, BiConsumer<Integer, String> consumer) {
        readUsingDelimiter(resource, "\n", charset, consumer);
    }


    public static void readLines(@NonNull String location, @NonNull Charset charset, @NonNull final Consumer<String> consumer) {
        readUsingDelimiter(location, "\n", charset, consumer);
    }

    public static void readLines(@NonNull String location, @NonNull Charset charset, @NonNull final BiConsumer<Integer, String> consumer) {
        readUsingDelimiter(location, "\n", charset, consumer);
    }

    public static void readLines(@NonNull String location, @NonNull Charset charset, @Nullable Predicate2<Integer, String> consumePredicate, @NonNull final BiConsumer<Integer, String> consumer, @Nullable Predicate2<Integer, String> breakPredicate) {
        readUsingDelimiter(location, "\n", charset, consumePredicate, consumer, breakPredicate);
    }

    public static String readFirstLine(@NonNull String location, @NonNull Charset charset) {
        final Holder<String> firstLine = new Holder<String>();
        readLines(location, charset, new Predicate2<Integer, String>() {
            @Override
            public boolean test(Integer index, String line) {
                return index == 0;
            }
        }, new BiConsumer<Integer, String>() {
            @Override
            public void accept(Integer index, String line) {
                firstLine.set(line);
            }
        }, new Predicate2<Integer, String>() {
            @Override
            public boolean test(Integer index, String value) {
                return index != 0;
            }
        });
        return firstLine.get();
    }

}
