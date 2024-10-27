package com.sondertara.common.codec;

public interface StringCodec extends Encoder<String,String>, Decoder<String,String> {
    @Override
    String decode(String source) throws CodecException;

    @Override
    String encode(String source) throws CodecException;
}
