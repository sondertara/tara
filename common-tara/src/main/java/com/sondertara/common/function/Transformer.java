package com.sondertara.common.function;

@FunctionalInterface
public interface Transformer<I, O> {
    O transform(I input);
}
