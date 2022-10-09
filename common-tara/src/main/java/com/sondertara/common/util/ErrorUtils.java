package com.sondertara.common.util;

/**
 * @author huangxiaohu
 */
public class ErrorUtils {

    public static IllegalArgumentException illegalArgumentException(String message, Object... args) {
        return new IllegalArgumentException(StringFormatter.format(message, args));
    }

    public static IllegalStateException illegalStateException(String message, Object... args) {
        return new IllegalStateException(StringFormatter.format(message, args));
    }
}
