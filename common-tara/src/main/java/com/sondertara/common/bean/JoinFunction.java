
package com.sondertara.common.bean;

/**
 * Defines how to compute Value1 and Value2 by their common Key
 * 
 * @author huangxiaohu
 * @param <K>  Key
 * @param <V1> Value1
 * @param <V2> Value2
 * @param <R>  Result
 */
public interface JoinFunction<K, V1, V2, R> {
  R compute(K key, V1 value1, V2 value2);
}
