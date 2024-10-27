package com.sondertara.common.collection.iter;

import org.jspecify.annotations.Nullable;

import java.util.Enumeration;
import java.util.Iterator;

public class IteratorEnumeration<E> implements Enumeration<E> {
    private Iterator<E> delegate;

    public IteratorEnumeration(@Nullable Iterator<E> iterator) {
        this.delegate = iterator;
    }

    @Override
    public boolean hasMoreElements() {
        return delegate != null && delegate.hasNext();
    }

    @Override
    public E nextElement() {
        return delegate != null ? delegate.next() : null;
    }
}
