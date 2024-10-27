package com.sondertara.common.hash;


import com.sondertara.common.base.Named;
import com.sondertara.common.function.Factory;

/**
 *  */
public interface Hasher extends Factory<Object, Hasher>, Named {
    /**
     * Calculate a hash using all bytes from the input argument, and
     * a seed of 0.
     *
     * @param bytes input bytes
     * @return hash value
     */
    long hash(byte[] bytes);

    /**
     * 一次性计算 hash
     * <p>
     * Calculate a hash using all bytes from the input argument,
     * and a provided seed value.
     *
     * @param bytes input bytes
     * @param seed  seed value
     * @return hash value
     */
    long hash(byte[] bytes, long seed);

    /**
     * 一次性计算 hash
     * <p>
     * Calculate a hash using bytes from 0 to <code>length</code>, and
     * the provided seed value
     *
     * @param bytes  input bytes
     * @param length length of the valid bytes to consider
     * @param seed   seed value
     * @return hash value
     */
    long hash(byte[] bytes, int length, long seed);

    @Override
    Hasher get(Object initParams);

    @Override
    String getName();
}
