package com.sondertara.common.function;

/**
 *  */
public interface Matcher<E,R> {
    R matches(E e);
}
