package com.sondertara.common.classpath.classloader;

import java.io.InputStream;

public interface ClassLoaderAccessor {
    Class loadClass(String fqcn);

    InputStream getResourceStream(String name);
}
