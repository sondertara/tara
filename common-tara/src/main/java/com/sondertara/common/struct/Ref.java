package com.sondertara.common.struct;

public interface Ref<T> extends Reference<T> {
    void set(T t);

    @Override
    T get();

    @Override
    boolean isNull();

    boolean isEmpty();
}
