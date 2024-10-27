package com.sondertara.common.classpath;

import com.sondertara.common.io.file.Filenames;

import java.util.Objects;

public class Classpaths {
    private Classpaths(){

    }
    public static String classNameToPath(String className) {
        Objects.requireNonNull(className, "className is null or empty");
        if (className.endsWith(".class")) {
            className = className.substring(0, className.length() - ".class".length());
        }
        className = className.replace(".", "/");
        className = className + ".class";
        return className;
    }

    public static String packageToPath(String packageName) {
        return packageName.replace(".", "/");
    }

    public static String getCanonicalFilePath(String path) {
        return Filenames.cleanAsUnixPath(path);
    }

}
