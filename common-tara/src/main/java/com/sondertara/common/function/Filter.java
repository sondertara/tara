package com.sondertara.common.function;

/**
 * 过滤器接口
 *
 * @author huangxiaohu
 */
@FunctionalInterface
public interface Filter<T> {
	/**
	 * 是否接受对象
	 *
	 * @param t 检查的对象
	 * @return 是否接受对象
	 */
	boolean accept(T t);
}