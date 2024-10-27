package com.sondertara.common.regex;

import com.sondertara.common.exception.SyntaxException;

/**
 *  */
public class NamedGroupConflictedException extends SyntaxException {

    public NamedGroupConflictedException() {
        super();
    }

    public NamedGroupConflictedException(String message) {
        super(message);
    }

    public NamedGroupConflictedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NamedGroupConflictedException(Throwable cause) {
        super(cause);
    }

}
