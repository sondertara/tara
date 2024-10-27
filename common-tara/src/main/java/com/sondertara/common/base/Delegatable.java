package com.sondertara.common.base;

public interface Delegatable<T> extends DelegateHolder<T> {
    @Override
    T getDelegate();

    void setDelegate(final T delegate);
}
