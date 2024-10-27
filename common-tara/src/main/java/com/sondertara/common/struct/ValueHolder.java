package com.sondertara.common.struct;

public interface ValueHolder<V> extends Ref<V> {
    void set(V v);
    void reset();
    @Override
    V get();
}
