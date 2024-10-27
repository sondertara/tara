package com.sondertara.common.hash.streaming.crc;


import com.sondertara.common.hash.AbstractHasher;
import com.sondertara.common.hash.UnsupportedHashAlgorithmException;
import com.sondertara.common.hash.streaming.AbstractStreamingHasher;

public class CrcHasher extends AbstractStreamingHasher {
    private CrcCalculator calculator;
    private long h;

    public CrcHasher(String name) {
        CrcAlgoMetadata metadata = CRCs.getCrcAlgoMetadata(name);
        if (metadata == null) {
            throw new UnsupportedHashAlgorithmException(name);
        }
        this.calculator = new CrcCalculator(metadata);
        reset();
    }

    @Override
    public void setSeed(long seed) {
        this.seed = this.calculator.getInit();
        this.h = this.seed;
    }

    @Override
    public void update(byte[] bytes, int off, int len) {
        this.h = this.calculator.update(this.h, bytes, off, len);
    }

    @Override
    public long getHash() {
        long r = this.calculator.getHashResult(this.h);
        reset();
        return r;
    }

    @Override
    protected AbstractHasher createInstance(Object initParams) {
        return new CrcHasher(this.calculator.getMetadata().getName());
    }

    @Override
    public String getName() {
        return this.calculator.getMetadata().getName();
    }
}
