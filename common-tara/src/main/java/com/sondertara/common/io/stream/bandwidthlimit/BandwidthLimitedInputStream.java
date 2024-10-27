package com.sondertara.common.io.stream.bandwidthlimit;

import com.sondertara.common.io.stream.IOStreamPipeline;
import com.sondertara.common.io.stream.WrappedInputStream;

import java.io.InputStream;

public class BandwidthLimitedInputStream extends WrappedInputStream {
    public BandwidthLimitedInputStream(InputStream in, BandwidthLimiter limiter) {
        super(in, IOStreamPipeline.of(new BandwidthLimitedInputStreamInterceptor(limiter)));
    }
}
