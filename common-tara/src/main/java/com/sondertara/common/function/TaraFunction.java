package com.sondertara.common.function;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 使Function获得序列化能力
 *
 * @param <T>
 * @param <R>
 * @author huangxiaohu
 */
@FunctionalInterface
public interface TaraFunction<T, R> extends Function<T, R>, Serializable {
}

