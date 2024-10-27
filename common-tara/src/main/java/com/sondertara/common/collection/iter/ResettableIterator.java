package com.sondertara.common.collection.iter;

import java.util.Iterator;

public interface ResettableIterator<E> extends Iterator<E> {
    void reset();
}
