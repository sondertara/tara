package com.sondertara.common.io.stream.bandwidthlimit;

import com.sondertara.common.io.stream.InputStreamInterceptor;

import java.io.InputStream;

public class BandwidthLimitedInputStreamInterceptor extends InputStreamInterceptor {

    private BandwidthLimiter bandwidthLimiter;

    public BandwidthLimitedInputStreamInterceptor(BandwidthLimiter bandwidthLimiter) {
        this.bandwidthLimiter = bandwidthLimiter;
    }

    @Override
    public boolean beforeRead(InputStream inputStream, byte[] b, int off, int len) {
        this.bandwidthLimiter.limitNextBytes(len);
        return true;
    }

    @Override
    public boolean afterRead(InputStream inputStream, byte[] b, int off, int len) {
        return true;
    }
}

