package com.sondertara.common.concurrent.threadlocal;

import com.sondertara.common.base.Delegatable;
import com.sondertara.common.function.Factory;

import java.util.Objects;

public class ThreadLocalFactory<I, E> implements Factory<I, E>, Delegatable<Factory<I, E>> {
    private final ThreadLocal<E> valueCache;
    private final ThreadLocal<I> inputCache = new ThreadLocal<I>();
    private Factory<I, E> delegate;

    public ThreadLocalFactory(final Factory<I, E> delegate) {
        setDelegate(Objects.requireNonNull(delegate));
        this.valueCache = ThreadLocal.withInitial(() -> ThreadLocalFactory.this.delegate.get(inputCache.get()));
    }

    @Override
    public Factory<I, E> getDelegate() {
        return delegate;
    }

    @Override
    public void setDelegate(Factory<I, E> delegate) {
        this.delegate = delegate;
    }

    @Override
    public E get(I input) {
        inputCache.set(input);
        try {
            valueCache.remove();
            return valueCache.get();
        } finally {
            inputCache.remove();
        }
    }

    public E get(){
        return valueCache.get();
    }

    public void clear() {
        valueCache.remove();
    }
}
