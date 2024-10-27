package com.sondertara.common.function;

import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface Parser<I, O> {
    @Nullable
    O parse(I input);
}
