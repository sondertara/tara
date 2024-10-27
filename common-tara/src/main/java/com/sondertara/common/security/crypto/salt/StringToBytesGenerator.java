package com.sondertara.common.security.crypto.salt;


import com.sondertara.common.text.StringUtils;

public class StringToBytesGenerator implements BytesSaltGenerator{
    private StringSaltGenerator delegate;

    public StringToBytesGenerator(StringSaltGenerator stringSaltGenerator){
        this.delegate=stringSaltGenerator;
    }

    @Override
    public byte[] apply(Integer bytesLength) {
        int charsLength = (bytesLength+1)/2;
        String str=delegate.apply(charsLength);
        return StringUtils.getBytesUtf8(str);
    }
}
