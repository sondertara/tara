package com.sondertara.common.exception;

import com.sondertara.common.text.ParameterizedMessage;

public class ExceptionMessage extends ParameterizedMessage {
    public ExceptionMessage(String msg) {
        super(msg);
    }

    public ExceptionMessage(String msg, Object... object) {
        super(msg, object);
    }

}
