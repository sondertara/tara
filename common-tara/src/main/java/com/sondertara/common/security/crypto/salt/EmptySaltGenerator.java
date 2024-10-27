package com.sondertara.common.security.crypto.salt;


import com.sondertara.common.base.Emptys;

public class EmptySaltGenerator implements BytesSaltGenerator{
    @Override
    public byte[] apply(Integer bytesLength) {
        return Emptys.EMPTY_BYTES;
    }
}
