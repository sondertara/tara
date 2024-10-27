package com.sondertara.common.io.file.validator;

import com.sondertara.common.text.StringUtils;
import com.sondertara.common.os.JdkUtils;

public class FilepathValidators {

    public static boolean validateName(String path) {
        return validateName(path, JdkUtils.IS_WINDOWS);
    }

    public static boolean validateName(String path, String osFamily) {
        return validateName(path, StringUtils.contains(osFamily, "win"));
    }

    private static boolean validateName(String path, boolean windows) {
        if (windows) {
            return WindowsFilepathValidator.INSTANCE.isLegalFilename(path);
        } else {
            return UnixFilepathValidator.INSTANCE.isLegalFilename(path);
        }
    }


    public static boolean validatePath(String path) {
        return validatePath(path, JdkUtils.IS_WINDOWS);
    }

    public static boolean validatePath(String path, String osFamily) {
        return validatePath(path, StringUtils.contains(osFamily, "win"));
    }

    private static boolean validatePath(String path, boolean windows) {
        if (windows) {
            return WindowsFilepathValidator.INSTANCE.isLegalFilepath(path);
        } else {
            return UnixFilepathValidator.INSTANCE.isLegalFilepath(path);
        }
    }

    private FilepathValidators(){}
}
