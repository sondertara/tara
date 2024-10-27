package com.sondertara.common.reflect;

@FunctionalInterface
public interface TypedPropertyGetter<T, V> {
	V get(T bean);
}
