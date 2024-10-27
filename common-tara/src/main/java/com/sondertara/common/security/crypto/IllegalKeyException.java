package com.sondertara.common.security.crypto;


import com.sondertara.common.text.StringUtils;

public class IllegalKeyException extends SecurityException {
    public IllegalKeyException() {
        super();
    }

    public IllegalKeyException(String algorithm, String key) {
        this(StringUtils.format("illegal {} key {}", algorithm, key));
    }

    public IllegalKeyException(String s) {
        super(s);
    }

    public IllegalKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalKeyException(Throwable cause) {
        super(cause);
    }
}
