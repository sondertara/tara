package com.sondertara.common.collection.sequence;

import com.sondertara.common.collection.Lists;

public class ArraySequence<E> extends ListSequence<E> {
    public ArraySequence(E[] array) {
        super(Lists.asList(array));
    }
}
