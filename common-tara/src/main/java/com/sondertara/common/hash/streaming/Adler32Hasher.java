package com.sondertara.common.hash.streaming;


import com.sondertara.common.hash.AbstractHasher;

import java.util.zip.Adler32;

/**
 *  */
public class Adler32Hasher extends ChecksumHasher {
    public Adler32Hasher() {
        super(new Adler32());
    }

    @Override
    protected AbstractHasher createInstance(Object initParam) {
        return new Adler32Hasher();
    }
}
