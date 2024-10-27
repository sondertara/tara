package com.sondertara.common.io.stream;

import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.function.Consumer4;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 *  */
public class WrappedOutputStream extends FilterOutputStream {
    private IOStreamPipeline pipeline;

    /**
     *      */
    public WrappedOutputStream(OutputStream out, IOStreamPipeline pipeline) {
        super(out);
        this.pipeline = pipeline;
    }

    /**
     *
     *      */
    public WrappedOutputStream(OutputStream out, List<Consumer4<OutputStream, byte[], Integer, Integer>> consumers) {
       this(out, IOStreamPipeline.ofOutputStreamConsumers(consumers));
    }

    @Override
    public void write(int b) throws IOException {
        byte[] bytes = new byte[]{(byte) b};
        write(bytes, 0, 1);
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (len > 0) {
            if (ObjectUtils.isNotNull(this.pipeline)) {
               this.pipeline.beforeWrite(this, b, off, len);
            }
            out.write(b, off, len);
            if (ObjectUtils.isNotNull(this.pipeline)) {
                this.pipeline.afterWrite(this, b, off, len);
            }
        }
    }
}
