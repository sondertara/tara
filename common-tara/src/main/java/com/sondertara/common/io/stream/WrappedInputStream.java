package com.sondertara.common.io.stream;

import com.sondertara.common.base.Emptys;
import com.sondertara.common.base.ObjectUtils;
import com.sondertara.common.function.Consumer4;
import com.sondertara.common.io.IOUtils;
import com.sondertara.common.math.Maths;
import com.sondertara.common.math.Unsigneds;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 *  */
public class WrappedInputStream extends FilterInputStream {
    private IOStreamPipeline pipeline;

    /**
     *      */
    public WrappedInputStream(InputStream in, IOStreamPipeline pipeline) {
        super(in);
        this.pipeline = pipeline;
    }

    /**
     *      */
    public WrappedInputStream(InputStream in, List<Consumer4<InputStream, byte[], Integer, Integer>> consumers) {
        this(in, IOStreamPipeline.ofInputStreamConsumers(consumers));
    }


    @Override
    public int read() throws IOException {
        if (ObjectUtils.isNotNull(this.pipeline)) {
            this.pipeline.beforeRead(this, Emptys.EMPTY_BYTES, 0, 1);
        }
        int b = super.read();

        if (ObjectUtils.isNotNull(this.pipeline) && b != -1) {
            final byte[] bs = new byte[]{Unsigneds.toSignedByte(b)};
            this.pipeline.afterRead(this, bs, 0, 1);
        }
        return b;
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        int l = Maths.min(len, IOUtils.DEFAULT_BUFFER_SIZE);
        if (ObjectUtils.isNotNull(this.pipeline)) {
            this.pipeline.beforeRead(this, b, off, l);
        }
        int length = super.read(b, off, l);

        if (ObjectUtils.isNotNull(this.pipeline) && length > 0) {
            this.pipeline.afterRead(this, b, off, length);
        }
        return length;
    }


    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public synchronized void mark(int readlimit) {

    }

    @Override
    public synchronized void reset() throws IOException {
        throw new UnsupportedOperationException();
    }
}
