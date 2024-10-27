package com.sondertara.common.timing.timer;


public interface Factory<I, O> {
    O get(I input);
}
