package com.sondertara.common.collection.iter;

import org.jspecify.annotations.Nullable;

import java.util.Enumeration;

/**
 * Wrap an Enumeration to an Iterator
 *
 * @param <E>
 */
public class EnumerationIterator<E> extends UnmodifiableIterator<E> {
    Enumeration<E> enumeration;

    public EnumerationIterator(@Nullable Enumeration<E> enumeration) {
        this.enumeration = enumeration;
    }

    @Override
    public boolean hasNext() {
        return enumeration != null && enumeration.hasMoreElements();
    }

    @Override
    public E next() {
        return enumeration != null ? enumeration.nextElement() : null;
    }
}
