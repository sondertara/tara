package com.sondertara.common.reflect.parameter;

import com.sondertara.common.reflect.Parameter;

import java.util.function.Function;

public interface ParameterSupplier<E, O extends Parameter<E>> extends Function<ParameterMeta, O> {
    /**
     * java.lang.reflect.Parameter 是在JDK 1.8才出现的
     * <p>
     * 所以这个方法是判断，底层是否是用的 JDK 提供的 Parameter API
     *
     * @return true if using jdk 1.8
     */
    boolean usingJdkApi();
}
