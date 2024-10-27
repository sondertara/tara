package com.sondertara.common.exception;

public class CodecException extends RuntimeException{
    public CodecException(String message) {
        super(message);
    }
    public CodecException(Throwable cause) {
        super(cause);
    }
    public CodecException(String message, Throwable cause) {
        super(message, cause);
    }
}
