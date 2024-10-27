package com.sondertara.common.text.tokenizer;

public class TokenizationException extends RuntimeException {
    public TokenizationException() {
        super();
    }

    public TokenizationException(String message) {
        super(message);
    }

    public TokenizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenizationException(Throwable cause) {
        super(cause);
    }
}
