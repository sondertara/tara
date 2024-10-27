package com.sondertara.common.io.resource;

import java.io.IOException;
import java.net.URL;

public interface Urlable {
    URL getUrl() throws IOException;
}
