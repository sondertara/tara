package com.sondertara.common.codec;

/**
 *  */
public interface StringifyEncoder<T> extends Encoder<T,String> {
    @Override
    String encode(T source);
}

