package com.sondertara.common.function;

/**
 * @author huangxiaohu.1ih
 */
@FunctionalInterface
public interface Factory<I, O>  {

    O get(I input);
}
