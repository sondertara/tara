package com.sondertara.common.collection.iter;

import java.util.ListIterator;

public class UnmodifiableListIterator<E> extends WrappedListIterator<E> {

    public UnmodifiableListIterator(ListIterator delegate){
        super(delegate,false);
    }

    @Override
    public void remove() {

    }

    @Override
    public void set(E e) {

    }

    @Override
    public void add(E e) {

    }
}
