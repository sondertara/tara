package com.sondertara.common.text;

public interface Formatter<I, O> {
    O format(I input, Object... args);
}
