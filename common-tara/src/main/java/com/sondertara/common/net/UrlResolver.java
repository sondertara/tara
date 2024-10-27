package com.sondertara.common.net;

import java.io.IOException;
import java.net.URL;

/**
 * Resolves container-specific URLs into standard Java URLs.
 */
public interface UrlResolver {
    /**
     * Resolves this container-specific URL into standard Java URL.
     *
     * @param url The URL to resolve.
     * @return The matching standard Java URL.
     * @throws IOException when the scanning failed.
     */
    URL toStandardJavaUrl(URL url) throws IOException;
}
