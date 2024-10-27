package com.sondertara.common.codec;

/**
 *  */
public interface BinaryStringifyEncoder extends StringifyEncoder<byte[]> {
    @Override
    String encode(byte[] bytes);
}
