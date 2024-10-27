package com.sondertara.common.io;

public interface WritableComparable<T> extends Writable, Comparable<T> {
    @Override
    int compareTo(T o);
}
