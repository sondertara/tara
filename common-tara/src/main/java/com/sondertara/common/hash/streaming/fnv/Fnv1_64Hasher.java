package com.sondertara.common.hash.streaming.fnv;


import com.sondertara.common.hash.AbstractHasher;
import com.sondertara.common.hash.streaming.AbstractStreamingHasher;

/**
 *  */
public class Fnv1_64Hasher extends AbstractStreamingHasher {
    private static final long INITIAL_VALUE = 0xcbf29ce484222325L;
    private static final long MULTIPLIER = 0x100000001b3L;

    private long hash = INITIAL_VALUE;


    @Override
    public void update(byte b) {
        hash *= MULTIPLIER;
        hash ^= 0xffL & b;
    }


    @Override
    public long getHash() {
        long h = hash;
        reset();
        return h;
    }

    @Override
    public void reset() {
        hash = INITIAL_VALUE;
    }

    @Override
    protected AbstractHasher createInstance(Object initParam) {
        return new Fnv1_64Hasher();
    }
}
