package com.sondertara.common.command;

/**
 * @author huangxiaohu
 */

public enum OSType {
    /**
     *
     */
    MAC, UNIX, LINUX, WINDOWS;

    public static OSType getOsType() {
        String property = System.getProperty("os.name");
        for (OSType value : OSType.values()) {
            if (property.toUpperCase().contains(value.toString())) {
                return value;
            }
        }
        return null;
    }
}
