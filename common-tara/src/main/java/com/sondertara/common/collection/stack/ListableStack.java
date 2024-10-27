package com.sondertara.common.collection.stack;

public class ListableStack<E> extends SimpleStack<E> {

    public E get(int index) {
        return this.list.get(index);
    }

}
