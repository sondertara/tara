package com.sondertara.common.classpath.scanner;

import com.sondertara.common.net.UrlResolver;

import java.io.IOException;
import java.net.URL;

/**
 * Default implementation of UrlResolver.
 */
public class DefaultUrlResolver implements UrlResolver {
    public URL toStandardJavaUrl(URL url) throws IOException {
        return url;
    }
}
