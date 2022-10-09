package com.sondertara.common.lang.resource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 资源接口定义<br>
 * <p>
 * 资源是数据表示的统称，我们可以将任意的数据封装为一个资源，然后读取其内容。
 * </p>
 * <p>
 * 资源可以是文件、URL、ClassPath中的文件亦或者jar(zip)包中的文件。
 * </p>
 * <p>
 * 提供资源接口的意义在于，我们可以使用一个方法接收任意类型的数据，从而处理数据，
 * 无需专门针对File、InputStream等写多个重载方法，同时也为更好的扩展提供了可能。
 * </p>
 * <p>
 * 使用非常简单，假设我们需要从classpath中读取一个xml，我们不用关心这个文件在目录中还是在jar中：
 * </p>
 * 
 * <pre>
 * Resource resource = new ClassPathResource("test.xml");
 * String xmlStr = resource.readUtf8Str();
 * </pre>
 * <p>
 * 同样，我们可以自己实现Resource接口，按照业务需要从任意位置读取数据，比如从数据库中。
 * </p>
 *
 * @author huangxiaohu
 * @since 3.2.1
 */
public interface Resource {

    /**
     * 获取资源名，例如文件资源的资源名为文件名
     *
     * @return 资源名
     * @since 4.0.13
     */
    String getName();

    /**
     * 获得解析后的{@link URL}，无对应URL的返回{@code null}
     *
     * @return 解析后的{@link URL}
     */
    URL getUrl();

    /**
     * 获得 {@link InputStream}
     *
     * @return {@link InputStream}
     */
    InputStream getStream();

    /**
     * 检查资源是否变更<br>
     * 一般用于文件类资源，检查文件是否被修改过。
     *
     * @return 是否变更
     * @since 5.7.21
     */
    default boolean isModified() {
        return false;
    }

    /**
     * 读取资源内容，读取完毕后会关闭流<br>
     * 关闭流并不影响下一次读取
     *
     * @param charset 编码
     * @return 读取资源内容
     */
    default String readStr(Charset charset) {
        try (InputStream inputStream = getStream(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
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
     * 读取资源内容，读取完毕后会关闭流<br>
     * 关闭流并不影响下一次读取
     *
     * @return 读取资源内容
     */
    default String readUtf8Str() {
        return readStr(StandardCharsets.UTF_8);
    }

}
