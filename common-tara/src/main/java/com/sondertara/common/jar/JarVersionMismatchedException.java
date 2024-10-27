package com.sondertara.common.jar;

import com.sondertara.common.text.StringUtils;

public class JarVersionMismatchedException extends RuntimeException {
    public JarVersionMismatchedException(String... jarNames) {
        this(StringUtils.join(",", jarNames));
    }

    public JarVersionMismatchedException() {
        super();
    }

    public JarVersionMismatchedException(String message) {
        super(message);
    }

    public JarVersionMismatchedException(String message, Throwable cause) {
        super(message, cause);
    }

    public JarVersionMismatchedException(Throwable cause) {
        super(cause);
    }

}
