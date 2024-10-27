package com.sondertara.common.jni;

import com.sondertara.common.os.JdkUtils;

public class NativeLibraryUtils {
    /**
     * Delegate the calling to {@link System#load(String)} or {@link System#loadLibrary(String)}.
     *
     * @param libName  - The native library path or name
     * @param absolute - Whether the native library will be loaded by path or by name
     */
    public static void loadLibrary(String libName, boolean absolute) {
        if (absolute) {
            System.load(libName);
        } else {
            System.loadLibrary(libName);
        }
    }

    public static String libExtension() {
        return JdkUtils.IS_WINDOWS ? ".dll" : ".so";
    }

    private NativeLibraryUtils() {
        // Utility
    }
}
