package com.sondertara.common.reflect;

@FunctionalInterface
public interface VoidMethod<T> {
	void invoke(T bean) throws Exception;
}
