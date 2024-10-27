package com.sondertara.common.random;

import java.util.function.Function;

public interface BytesRandom extends Function<Integer, byte[]> {
    /**
     * 生成一个随机的bytes数组填充到 dest中
     */
    void get(byte[] dest);

    /**
     * 基于指定的 size 生成一个 byte[] ，生成的byte[] 的length 不一定等于 size
     */
    byte[] get(Integer size);

    @Override
    default byte[] apply(Integer integer) {
        return get(integer);
    }
}