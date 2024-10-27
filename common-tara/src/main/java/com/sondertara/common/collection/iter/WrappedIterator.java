package com.sondertara.common.collection.iter;

import org.jspecify.annotations.NonNull;

import java.util.Iterator;
import java.util.Objects;

public class WrappedIterator<E> implements Iterator<E> {
    private Iterator<E> delegate;
    private boolean mutable;

    public WrappedIterator(@NonNull Iterator<E> delegate, boolean mutable) {
        Objects.requireNonNull(delegate);
        this.delegate = delegate;
        this.mutable = mutable;
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    @Override
    public E next() {
        return delegate.next();
    }

    @Override
    public void remove() {
        if (!mutable) {
            throw new UnsupportedOperationException("Unsupported remove() on an immutable iterator");
        }
    }
}
