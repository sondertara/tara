package com.sondertara.common.util;

import com.sondertara.common.exception.TaraException;
import com.sondertara.common.function.Filter;
import com.sondertara.common.io.FileUtils;
import com.sondertara.common.lang.Assert;
import com.sondertara.common.lang.map.EnumerationIter;
import com.sondertara.common.lang.resource.ClassPathResource;
import com.sondertara.common.lang.resource.FileResource;
import com.sondertara.common.lang.resource.Resource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Resource资源工具类
 *
 * @author huangxiaohu
 */
public class ResourceUtils {

    /**
     * 读取Classpath下的资源为字符串，使用UTF-8编码
     *
     * @param resource 资源路径，使用相对ClassPath的路径
     * @return 资源内容
     * @since 3.1.1
     */
    public static String readUtf8Str(String resource) {
        return readStr(resource, StandardCharsets.UTF_8);
    }

    /**
     * 读取Classpath下的资源为字符串
     *
     * @param resource 可以是绝对路径，也可以是相对路径（相对ClassPath）
     * @param charset  编码
     * @return 资源内容
     * @since 3.1.1
     */
    public static String readStr(String resource, Charset charset) {
        try (InputStream inputStream = getStream(resource);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            int bytes;
            while ((bytes = inputStream.read()) != -1) {
                outputStream.write(bytes);
            }
            return outputStream.toString(charset.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从ClassPath资源中获取{@link InputStream}
     *
     * @param resource ClassPath资源
     * @return {@link InputStream}
     * @since 3.1.2
     */
    public static InputStream getStream(String resource) throws IOException {
        return getResourceObj(resource).getStream();
    }

    /**
     * 获得资源的URL<br>
     * 路径用/分隔，例如:
     *
     * <pre>
     * config/a/db.config
     * spring/xml/test.xml
     * </pre>
     *
     * @param resource 资源（相对Classpath的路径）
     * @return 资源URL
     */
    public static URL getResource(String resource) {
        return getResource(resource, null);
    }

    /**
     * 获取指定路径下的资源列表<br>
     * 路径格式必须为目录格式,用/分隔，例如:
     *
     * <pre>
     * config/a
     * spring/xml
     * </pre>
     *
     * @param resource 资源路径
     * @return 资源列表
     */
    public static List<URL> getResources(String resource) {
        return getResources(resource, null);
    }

    /**
     * 获取指定路径下的资源列表<br>
     * 路径格式必须为目录格式,用/分隔，例如:
     *
     * <pre>
     * config/a
     * spring/xml
     * </pre>
     *
     * @param resource 资源路径
     * @param filter   过滤器，用于过滤不需要的资源，{@code null}表示不过滤，保留所有元素
     * @return 资源列表
     */
    public static List<URL> getResources(String resource, Filter<URL> filter) {

        List<URL> urls = new ArrayList<>();
        EnumerationIter<URL> iter = getResourceIter(resource);
        while (iter.hasNext()) {
            URL next = iter.next();
            if (filter.accept(next)) {
                urls.add(next);
            }
        }
        return urls;
    }

    /**
     * 获取指定路径下的资源Iterator<br>
     * 路径格式必须为目录格式,用/分隔，例如:
     *
     * <pre>
     * config/a
     * spring/xml
     * </pre>
     *
     * @param resource 资源路径
     * @return 资源列表
     * @since 4.1.5
     */
    public static EnumerationIter<URL> getResourceIter(String resource) {
        final Enumeration<URL> resources;
        try {
            resources = ClassLoaderUtils.getClassLoader().getResources(resource);
        } catch (IOException e) {
            throw new TaraException(e);
        }
        return new EnumerationIter<>(resources);
    }

    /**
     * 获得资源相对路径对应的URL
     *
     * @param resource  资源相对路径，{@code null}和""都表示classpath根路径
     * @param baseClass 基准Class，获得的相对路径相对于此Class所在路径，如果为{@code null}则相对ClassPath
     * @return {@link URL}
     */
    public static URL getResource(String resource, Class<?> baseClass) {
        resource = StringUtils.nullToEmpty(resource);
        return (null != baseClass) ? baseClass.getResource(resource)
                : ClassLoaderUtils.getClassLoader().getResource(resource);
    }

    /**
     * 获取 Resource 资源对象<br>
     *
     * @param path 路径，可以是绝对路径，也可以是相对路径（相对ClassPath）
     * @since 3.2.1
     */
    public static Resource getResourceObj(String path) {
        if (StringUtils.isNotBlank(path)) {
            if (path.startsWith(URLUtils.FILE_URL_PREFIX) || FileUtils.isAbsolutePath(path)) {
                return new FileResource(path);
            }
        }
        return new ClassPathResource(path);
    }

    public static File getFile(URL resourceUrl, String description) {
        Assert.notNull(resourceUrl, "Resource URL must not be null");
        if (!URLUtils.URL_PROTOCOL_FILE.equals(resourceUrl.getProtocol())) {
            throw new RuntimeException(description + " cannot be resolved to absolute file path "
                    + "because it does not reside in the file system: " + resourceUrl);
        }
        return new File(URLUtils.toURI(resourceUrl).getSchemeSpecificPart());
    }

}
