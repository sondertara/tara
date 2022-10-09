package com.sondertara.common.lang.resource;

import com.sondertara.common.exception.TaraException;
import com.sondertara.common.io.FileUtils;
import com.sondertara.common.util.ObjectUtils;
import com.sondertara.common.util.URLUtils;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;

/**
 * URL资源访问类
 *
 * @author huangxiaohu
 */
public class UrlResource implements Resource, Serializable {
    private static final long serialVersionUID = 1L;

    protected URL url;
    private long lastModified = 0;
    protected String name;

    // --------------------------------------------------------------------------------------
    // Constructor start

    /**
     * 构造
     *
     * @param uri URI
     * @since 5.7.21
     */
    public UrlResource(URI uri) {
        this(URLUtils.url(uri), null);
    }

    /**
     * 构造
     *
     * @param url URL
     */
    public UrlResource(URL url) {
        this(url, null);
    }

    /**
     * 构造
     *
     * @param url  URL，允许为空
     * @param name 资源名称
     */
    public UrlResource(URL url, String name) {
        this.url = url;
        if (null != url && URLUtils.URL_PROTOCOL_FILE.equals(url.getProtocol())) {
            this.lastModified = FileUtils.file(url).lastModified();
        }
        this.name = ObjectUtils.defaultIfNull(name, () -> (null != url ? FileUtils.getName(url.getPath()) : null));
    }

    /**
     * 构造
     *
     * @param file 文件路径
     * @deprecated Please use {@link FileResource}
     */
    @Deprecated
    public UrlResource(File file) {
        this.url = URLUtils.getURL(file);
    }
    // --------------------------------------------------------------------------------------
    // Constructor end

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public URL getUrl() {
        return this.url;
    }

    @Override
    public InputStream getStream() {
        if (null == this.url) {
            throw new TaraException("Resource URL is null!");
        }
        return URLUtils.getStream(url);
    }

    @Override
    public boolean isModified() {
        // lastModified == 0表示此资源非文件资源
        return (0 != this.lastModified) && this.lastModified != getFile().lastModified();
    }

    /**
     * 获得File
     *
     * @return {@link File}
     */
    public File getFile() {
        return FileUtils.file(this.url);
    }

    /**
     * 返回路径
     *
     * @return 返回URL路径
     */
    @Override
    public String toString() {
        return (null == this.url) ? "null" : this.url.toString();
    }
}
