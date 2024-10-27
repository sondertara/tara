package com.sondertara.common.security.crypto.salt;

public class FixedBytesSaltGenerator implements BytesSaltGenerator {
    private byte[] salt;

    public FixedBytesSaltGenerator(byte[] salt){
        this.salt=salt;
    }
    @Override
    public byte[] apply(Integer bytesLength) {
        return salt;
    }
}
