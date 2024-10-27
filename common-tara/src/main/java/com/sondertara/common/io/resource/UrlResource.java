package com.sondertara.common.io.resource;

import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.io.file.Files;
import com.sondertara.common.net.URLs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Objects;

/**
 * ftp://ftp.baidu.com/resources/xx
 * file:///home/fjn/
 * file://c:\\a\\b
 * http://www.baidu.com/resources/xx
 * jar:file://
 */
public class UrlResource extends AbstractLocatableResource<URL> {

    private URL url;

    public UrlResource(String url) {
        this(URLs.newURL(url));
    }

    public UrlResource(URI uri) {
        this(URLs.toURL(uri));
    }



    public UrlResource(URL url) {
        Objects.requireNonNull(url);
        this.url = url;
        String protocol = url.getProtocol();
        String prefix = URLs.getUrlPrefix(protocol);
        String path = url.toString();
        if(path.startsWith(prefix)){
           path = path.substring(prefix.length());
        }
        setLocation(prefix, path);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return URLs.getInputStream(url);
    }

    @Override
    public boolean exists() {
        return URLs.exists(url);
    }

    @Override
    public String getAbsolutePath() {
        if (URLs.isFileURL(url)) {
            File file = URLs.getFile(url);
            if (file != null) {
                return Files.getCanonicalPath(file);
            }
        }
        return url.getPath();
    }

    @Override
    public URL getRealResource() {
        return url;
    }

    @Override
    public long contentLength() {
        return URLs.getContentLength(url);
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UrlResource)) {
            return false;
        }
        UrlResource o2 = (UrlResource) obj;
        return ObjectUtils.equals(this.url, o2.url);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.url);
    }
}
