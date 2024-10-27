package com.sondertara.common.io.stream.bandwidthlimit;

import com.sondertara.common.io.stream.IOStreamPipeline;
import com.sondertara.common.io.stream.WrappedOutputStream;

import java.io.OutputStream;

public class BandwidthLimitedOutputStream extends WrappedOutputStream {
    public BandwidthLimitedOutputStream(OutputStream out, BandwidthLimiter limiter) {
        super(out, IOStreamPipeline.of(new BandwidthLimitedOutputStreamInterceptor(limiter)));
    }
}
