package com.sondertara.common.comparator;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public class NonZeroComparator<E> implements Comparator<E>, Serializable {
    private static final long serialVersionUID = 1L;
    private transient Comparator<E> delegate;

    public NonZeroComparator(Comparator<E> comparator) {
        Objects.requireNonNull(comparator);
        this.delegate = comparator;
    }

    @Override
    public int compare(E o1, E o2) {
        int delta = delegate.compare(o1, o2);
        return delta == 0 ? 1 : delta;
    }
}
