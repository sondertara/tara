package com.sondertara.common.function;

/**
 * 规约接口
 *
 * @author Jiahang Li
 * @version 1.0.0
 *  */
@FunctionalInterface
public interface Reduce<V, R> {

    /**
     * 规约
     *
     * @param v input
     * @return output
     */
    R accept(V[] v);

}
