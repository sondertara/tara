package com.sondertara.common.base;

import com.sondertara.common.function.Factory;

public interface Provider<I, O> extends Factory<I, O> {
    @Override
    O get(I input);
}
