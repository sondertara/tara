package com.sondertara.common.function;

import java.io.Serializable;

/**
 * 无参数的函数对象<br>
 * 接口灵感来自于<a href="http://actframework.org/">ActFramework</a><br>
 * 一个函数接口代表一个一个函数，用于包装一个函数为对象<br>
 * 在JDK8之前，Java的函数并不能作为参数传递，也不能作为返回值存在，此接口用于将一个函数包装成为一个对象，从而传递对象
 *
 * @author huangxiaohu
 *
 * @param <R> 返回值类型
 * @since 4.5.2
 */
@FunctionalInterface
public interface Func0<R> extends Serializable {
	/**
	 * 执行函数
	 *
	 * @return 函数执行结果
	 */
	R call();

}
